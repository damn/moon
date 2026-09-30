(ns moon.game
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math :as math]
            [clojure.string :as str]
            [moon.scene2d.group :as group]
            [moon.scene2d.table :as table]
            [moon.scene2d.window :as window]
            [moon.camera :as orthographic-camera]
            [moon.color :as color]
            [moon.tiled-map :as moon-tiled-map]
            [moon.viewport :as viewport]
            [moon.audio :as audio]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.coll :as coll]
            [moon.db :as db]
            [moon.effect-ctx :as effect-ctx]
            [moon.error-window :as error-window]
            [moon.faction :as faction]
            [moon.g2d :as moon-g2d]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.m :as m]
            [moon.malli :as malli-schema]
            [moon.mods :as mods]
            [moon.world :as world]
            [moon.number :as number]
            [moon.rand :as rand]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.string :as string]
            [moon.textures :as textures]
            [moon.throwable :as throwable]
            [moon.timer :as timer]
            [moon.v2 :as v2]
             [moon.val-max :as val-max]
             [qrecord.core :as q]
             [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx Application ApplicationListener Files Gdx Graphics Input Input$Buttons Input$Keys InputProcessor)
           (clojure Stage)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors Cursor GL20 Pixmap Pixmap$Format Texture Texture$TextureFilter TextureData)
           (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.graphics.glutils PixmapTextureData)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor Event Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup HorizontalGroup Image ImageButton Label ScrollPane Skin Stack TextButton TextTooltip TooltipManager Widget)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener ClickListener Drawable Layout TextureRegionDrawable)
           (com.badlogic.gdx.utils Align Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (space.earlygrey.shapedrawer ShapeDrawer))
  (:gen-class))

(def schema
  (malli-schema/create
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/textures :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/active-entities :any]
    [:ctx/delta-time :any]
    [:ctx/mouseover-eid :any]
    [:ctx/colors :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-speed :some]
    [:ctx/render-z-order :some]
    [:ctx/world :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some]
    [:ctx/elapsed-time :some]
    [:ctx/player-eid :some]
    [:ctx/paused? :some]
    [:ctx/show-potential-field-colors? :any]
    [:ctx/show-cell-entities? :boolean]
    [:ctx/show-cell-occupied? :boolean]
    [:ctx/show-body-bounds? :boolean]
    [:ctx/show-tile-grid? :boolean]]))

(q/defrecord Record [])

(q/defrecord EntityRecord [])

(def minimum-size 0.39)

(def max-delta 0.04)

(def level-fn uf-caves/create)

(def pausing? true)

(def state->pause-game?
  {:active-skill false
   :stunned false
   :player-moving false
   :player-idle true
   :player-dead true
   :player-item-on-cursor true})

(def factions-iterations
  {:good 15
   :evil 5})

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

(def world-unit-scale (float (/ 48)))

(def z-orders
  [:z-order/on-ground
   :z-order/ground
   :z-order/flying
   :z-order/effect])

(defn affected-targets
  [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(defn projectile-start-point [entity direction size]
  (v2/add (:entity/position entity)
          (v2/scale direction
                    (+ (/ (:entity/width entity) 2) size 0.1))))

(defn- add-text-effect [entity elapsed-time text duration]
  (assoc entity :entity/string-effect
         (if-let [existing (:entity/string-effect entity)]
           (-> existing
               (update :text str "\n" text)
               (update :counter timer/increment duration))
           {:text text
            :counter (timer/create elapsed-time duration)})))

(defn effect-applicable?
  [[k v] effect-ctx]
  (case k
    :effects/audiovisual
    (effect-ctx/get-target-position effect-ctx)

    :effects/projectile
    (effect-ctx/get-target-direction effect-ctx)

    :effects/spawn
    (and (:entity/faction @(effect-ctx/get-source effect-ctx))
         (effect-ctx/get-target-position effect-ctx))

    :effects/target-all
    true

    :effects/target-entity
    (and (effect-ctx/get-target effect-ctx)
         (seq (filter #(effect-applicable? % effect-ctx) (:entity-effects v))))

    :effects.target/audiovisual
    (effect-ctx/get-target effect-ctx)

    :effects.target/convert
    (let [source (effect-ctx/get-source effect-ctx)
          target (effect-ctx/get-target effect-ctx)]
      (and target
           (= (:entity/faction @target)
              (faction/enemy (:entity/faction @source)))))

    :effects.target/damage
    (and (effect-ctx/get-target effect-ctx)
         #_(:stats/hp @target))

    :effects.target/kill
    (and (effect-ctx/get-target effect-ctx)
         (:entity/fsm @(effect-ctx/get-target effect-ctx)))

    :effects.target/melee-damage
    (effect-applicable? [:effects.target/damage (stats/melee-damage @(effect-ctx/get-source effect-ctx))]
                        effect-ctx)

    :effects.target/spiderweb
    (:entity/stats @(effect-ctx/get-target effect-ctx))

    :effects.target/stun
    (and (effect-ctx/get-target effect-ctx)
         (:entity/fsm @(effect-ctx/get-target effect-ctx)))))

(defn effect-useful?
  [[k v] effect-ctx ctx]
  (case k
    :effects/audiovisual
    false

    :effects/projectile
    (let [{:keys [projectile/max-range] :as projectile} v
          raycaster (:ctx/raycaster ctx)
          source-p (:entity/position @(effect-ctx/get-source effect-ctx))
          target-p (:entity/position @(effect-ctx/get-target effect-ctx))]
      (and (not (let [[start1 target1 start2 target2] (v2/double-ray-endpositions source-p
                                                                                 target-p
                                                                                 (:projectile/size projectile))]
                  (or
                   (raycaster/blocked? raycaster start1 target1)
                   (raycaster/blocked? raycaster start2 target2))))
           (< (v2/distance source-p target-p)
              max-range)))

    :effects/target-all
    false

    :effects/target-entity
    (body/in-range? @(effect-ctx/get-source effect-ctx)
                    @(effect-ctx/get-target effect-ctx)
                    (:maxrange v))

    :effects.target/audiovisual
    false

    true))

(defn- skill-usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (stats/not-enough-mana? (:entity/stats entity) skill)
   :not-enough-mana

   (not (seq (filter #(effect-applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))

(def info
  {:k->fn {:creature/level (fn [v _elapsed-time]
                              (str "Level: " v))
           :entity/stats (fn [entity-stats _elapsed-time]
                           (stats/format-text entity-stats))
           :effects.target/convert (fn [_ _elapsed-time]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _elapsed-time]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _elapsed-time]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _elapsed-time]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _elapsed-time]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _elapsed-time]
                                  (str "Stuns for " (number/readable duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _elapsed-time]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _elapsed-time]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter elapsed-time]
                                             (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _elapsed-time]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _elapsed-time]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _elapsed-time]
                              (mods/format-text mods))
           :entity/skills (fn [skills _elapsed-time]
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _elapsed-time]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} elapsed-time]
                                    (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _elapsed-time]
                                   "Piercing")
           :property/pretty-name (fn [v _elapsed-time]
                                    v)
           :skill/cooling-down? (fn [counter elapsed-time]
                                   (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _elapsed-time]
                                 (str "Action-Time: " (number/readable v) " seconds"))
           :skill/action-time-modifier-key (fn [v _elapsed-time]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _elapsed-time]
                             (str "Cooldown: " (number/readable v) " seconds"))
           :skill/cost (fn [v _elapsed-time]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _elapsed-time]
                       (str "Range: " v " Meters."))}
   :k-order [:property/pretty-name
             :skill/action-time-modifier-key
             :skill/action-time
             :skill/cooldown
             :skill/cost
             :skill/effects
             :entity/species
             :creature/level
             :entity/stats
             :entity/delete-after-duration
             :projectile/piercing?
             :entity/projectile-collision
             :maxrange
             :entity-effects]
   :k->colors {:property/pretty-name "PRETTY_NAME"
               :stats/modifiers "CYAN"
               :maxrange "LIGHT_GRAY"
               :creature/level "GRAY"
               :projectile/piercing? "LIME"
               :skill/action-time-modifier-key "VIOLET"
               :skill/action-time "GOLD"
               :skill/cooldown "SKY"
               :skill/cost "CYAN"
               :entity/delete-after-duration "LIGHT_GRAY"
               :entity/faction "SLATE"
               :entity/fsm "YELLOW"
               :entity/species "LIGHT_GRAY"
               :entity/temp-modifier "LIGHT_GRAY"}})

(defn info-text
  [entity elapsed-time]
  (let [{:keys [k->fn
                k-order
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
                                   (str (info-fn v elapsed-time)))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (coll/sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable _t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (info-text v elapsed-time))))))
         (str/join "\n")
         string/remove-newlines)))

(defn- prepare-entity-geometry [entity]
  (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
    (assert position)
    (assert width)
    (assert height)
    (assert (>= width  (if collides? minimum-size 0)))
    (assert (>= height (if collides? minimum-size 0)))
    (assert (or (boolean? collides?) (nil? collides?)))
    (assert ((set z-orders) z-order))
    (assert (or (nil? rotation-angle)
                (<= 0 rotation-angle 360)))
    (assoc entity
           :entity/position (mapv float position)
           :entity/width (float width)
           :entity/height (float height)
           :entity/rotation-angle (or rotation-angle 0))))

(defn create-component
  [elapsed-time k v]
  (case k
    :entity/animation
    (let [{:keys [animation/frames
                  animation/frame-duration
                  animation/looping?
                  delete-after-stopped?]} v]
      (assert (not (and looping? delete-after-stopped?)))
      {:frames (vec frames)
       :frame-duration frame-duration
       :looping? looping?
       :cnt 0
       :maxcnt (* (count frames) (float frame-duration))
       :delete-after-stopped? delete-after-stopped?})

    :entity/delete-after-duration
    (timer/create elapsed-time v)

    :entity/projectile-collision
    (assoc v :already-hit-bodies #{})

    :entity/stats
    (-> v
        (update :stats/mana (fn [v] [v v]))
        (update :stats/hp   (fn [v] [v v])))

    v))

(defmulti create-entity-state
  (fn [[k _v] _eid _elapsed-time]
    k))

(defmethod create-entity-state :default
  [[_k v] _eid _elapsed-time]
  v)

(defmethod create-entity-state :active-skill
  [[_k [skill effect-ctx]] eid elapsed-time]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (stats/apply-action-speed-modifier (:entity/stats @eid) skill)
                 (timer/create elapsed-time))})

(defmethod create-entity-state :stunned
  [[_k duration] _eid elapsed-time]
  {:counter (timer/create elapsed-time duration)})

(defmethod create-entity-state :player-moving
  [[_k movement-vector] _eid _elapsed-time]
  {:movement-vector movement-vector})

(defmethod create-entity-state :npc-moving
  [[_k movement-vector] eid elapsed-time]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-value (:entity/stats @eid) :stats/reaction-time)
                           0.016))})

(defmethod create-entity-state :player-item-on-cursor
  [[_k item] _eid _elapsed-time]
  {:item item})

(def fsms
  {:npc (fsm/fsm-inc
          [[:npc-sleeping
            :kill -> :npc-dead
            :stun -> :stunned
            :alert -> :npc-idle]
           [:npc-idle
            :kill -> :npc-dead
            :stun -> :stunned
            :start-action -> :active-skill
            :movement-direction -> :npc-moving]
           [:npc-moving
            :kill -> :npc-dead
            :stun -> :stunned
            :timer-finished -> :npc-idle]
           [:active-skill
            :kill -> :npc-dead
            :stun -> :stunned
            :action-done -> :npc-idle]
           [:stunned
            :kill -> :npc-dead
            :effect-wears-off -> :npc-idle]
           [:npc-dead]])
   :player (fsm/fsm-inc
            [[:player-idle
              :kill -> :player-dead
              :stun -> :stunned
              :start-action -> :active-skill
              :pickup-item -> :player-item-on-cursor
              :movement-input -> :player-moving]
             [:player-moving
              :kill -> :player-dead
              :stun -> :stunned
              :no-movement-input -> :player-idle]
             [:active-skill
              :kill -> :player-dead
              :stun -> :stunned
              :action-done -> :player-idle]
             [:stunned
              :kill -> :player-dead
              :effect-wears-off -> :player-idle]
             [:player-item-on-cursor
              :kill -> :player-dead
              :stun -> :stunned
              :drop-item -> :player-idle
              :dropped-item -> :player-idle]
             [:player-dead]])})

(defn- create-fsm
  [fsm initial-state]
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))

(defn- create-action-bar []
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (HorizontalGroup.)
                                  (.space (float 2))
                                  (.pad (float 2))
                                  (.setName "moon.ui.action-bar.horizontal-group")
                                  (.setUserObject (doto (ButtonGroup.)
                                                    (.setMaxCheckCount (int 1))
                                                    (.setMinCheckCount (int 0)))))
                         :expand? true
                         :bottom? true}]]})
    (.setFillParent true)
    (.setName "moon.ui.action-bar")))

(defn- action-bar-get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject ^Actor group)}))

(defn- action-bar-add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (doto (ImageButton.
                      (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                        (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                                     (* scale (.getRegionHeight ^TextureRegion texture-region)))))
                 (.addListener (TextTooltip. ^String tooltip-text ^Skin skin))
                 (.setUserObject skill-id))]
    (group/add-actor! horizontal-group button)
    (.add ^ButtonGroup button-group ^Button button)
    nil))

(defn- action-bar-remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (get horizontal-group skill-id)]
    (.remove ^Actor button)
    (.remove ^ButtonGroup button-group ^Button button)
    nil))

(defn- action-bar-selected-skill [action-bar]
  (when-let [skill-button (.getChecked ^ButtonGroup (:button-group (action-bar-get-data action-bar)))]
    (.getUserObject ^Actor skill-button)))

(defn- inventory-window-get-cell [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn- inventory-window-remove-item! [inventory-window cell]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn- inventory-window-set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn- set-item [entity cell item]
  (assert (and (nil? (get-in (:entity/inventory entity) cell))
               (inventory/valid-slot? cell item)))
  (cond-> (assoc-in entity (cons :entity/inventory cell) item)
    (inventory/applies-modifiers? cell)
    (update :entity/stats stats/add-mods (:stats/modifiers item))))

(defn- set-item! [ctx eid cell item]
  (swap! eid set-item cell item)
  (when (:entity/player? @eid)
    (let [skin (:ctx/skin ctx)
          stage (:ctx/stage ctx)
          textures (:ctx/textures ctx)]
      (-> (.getRoot ^Stage stage)
          (#(group/find-actor % "moon.ui.windows.inventory"))
          (inventory-window-set-item! cell
                                    {:texture-region (textures/texture-region textures (:entity/image item))
                                     :tooltip-text (item/info-text item)}
                                    skin)))))

(defn- pickup-item! [ctx eid item]
  (assert (item/valid? item))
  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
    (assert cell)
    (assert (nil? cell-item))
    (set-item! ctx eid cell item)))

(defn- remove-item! [ctx eid cell]
  (let [entity @eid
        item (get-in (:entity/inventory entity) cell)]
    (assert item)
    (swap! eid assoc-in (cons :entity/inventory cell) nil)
    (when (inventory/applies-modifiers? cell)
      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
    (when (:entity/player? @eid)
      (-> (.getRoot ^Stage (:ctx/stage ctx))
          (#(group/find-actor % "moon.ui.windows.inventory"))
          (inventory-window-remove-item! cell)))))

(defn after-create-component
  [ctx eid [k v]]
  (case k
    :entity/fsm
    (let [{:keys [fsm initial-state]} v]
      (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
      (swap! eid assoc initial-state (create-entity-state [initial-state nil] eid (:ctx/elapsed-time ctx)))
      nil)

    :entity/skills
    (do
      (swap! eid assoc :entity/skills nil)
      (doseq [{:keys [property/id] :as skill} v]
        (assert (not (contains? (:entity/skills @eid) id)))
        (swap! eid update :entity/skills assoc id skill)
        (when (:entity/player? @eid)
          (let [skin (:ctx/skin ctx)
                stage (:ctx/stage ctx)
                textures (:ctx/textures ctx)]
            (-> (.getRoot ^Stage stage)
                (#(group/find-actor % "moon.ui.action-bar"))
                (action-bar-add-skill! {:skill-id (:property/id skill)
                                        :texture-region (textures/texture-region textures (:entity/image skill))
                                        :tooltip-text (info-text skill (:ctx/elapsed-time ctx))}
                                       skin)))))
      nil)

    :entity/inventory
    (do
      (swap! eid assoc :entity/inventory (->> #:inventory.slot{:bag      [6 4]
                                                              :weapon   [1 1]
                                                              :shield   [1 1]
                                                              :helm     [1 1]
                                                              :chest    [1 1]
                                                              :leg      [1 1]
                                                              :glove    [1 1]
                                                              :boot     [1 1]
                                                              :cloak    [1 1]
                                                              :necklace [1 1]
                                                              :rings    [2 1]}
                                                 (map (fn [[slot [width height]]]
                                                        [slot (moon-g2d/create width height (constantly nil))]))
                                                 (into {})))
      (doseq [item v]
        (pickup-item! ctx eid item))
      nil)

    nil))

(defn- item-place-position [player-position world-mouse-position maxrange]
  (v2/add player-position
          (v2/scale (v2/direction player-position world-mouse-position)
                    (min maxrange
                         (v2/distance player-position world-mouse-position)))))

(defn- mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn- button?
  [actor]
  (let [button-class? (fn [a] (some #(= Button %) (supers (class a))))]
    (or (button-class? actor)
        (when-let [parent (.getParent ^Actor actor)]
          (button-class? parent)))))

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)
                            (= "inventory-cell" (.getName ^com.badlogic.gdx.scenes.scene2d.Actor (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)))
                            (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (window/title-bar? actor)
      [:mouseover-actor/window-title-bar]

      (button? actor)
      [:mouseover-actor/button]

      :else
      [:mouseover-actor/unspecified])))

(defn- spawn-entity! [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (create-component (:ctx/elapsed-time ctx) k v)))
                       {}
                       entity)
        entity (prepare-entity-geometry entity)
        entity (merge (map->EntityRecord {}) entity)
        eid (atom entity)]
    (world/register-eid! (:ctx/world ctx) eid)
    (doseq [component @eid]
      (after-create-component ctx eid component))))

(defn- spawn-creature! [ctx {:keys [position creature-property components]}]
  (assert creature-property)
  (spawn-entity! ctx
                 (-> creature-property
                     (assoc :entity/position position
                            :entity/collides? true
                            :entity/z-order (if (:entity/flying? creature-property)
                                              :z-order/flying
                                              :z-order/ground))
                     (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
                     (m/safe-merge components))))

(defn- spawn-effect! [ctx position components]
  (spawn-entity! ctx
                 (assoc components
                        :entity/width 0.5
                        :entity/height 0.5
                        :entity/z-order :z-order/effect
                        :entity/position position)))

(defn- spawn-alert! [ctx position faction duration]
  (spawn-effect! ctx position
                 {:entity/alert-friendlies-after-duration
                  {:counter (timer/create (:ctx/elapsed-time ctx) duration)
                   :faction faction}}))

(defn- spawn-item! [ctx position item]
  (spawn-entity! ctx
                 {:entity/position position
                  :entity/width 0.75
                  :entity/height 0.75
                  :entity/z-order :z-order/on-ground
                  :entity/image (:entity/image item)
                  :entity/item item
                  :entity/clickable {:type :clickable/item
                                     :text (:property/pretty-name item)}}))

(defn- spawn-line! [ctx {:keys [start end duration color thick?]}]
  (spawn-effect! ctx start
                 {:entity/line-render {:thick? thick? :end end :color color}
                  :entity/delete-after-duration duration}))

(defn- spawn-projectile!
  [ctx
   {:keys [position direction faction]}
   {:keys [entity/image
           projectile/max-range
           projectile/speed
           entity-effects
           projectile/size
           projectile/piercing?]}]
  (spawn-entity! ctx
                 {:entity/position position
                  :entity/width size
                  :entity/height size
                  :entity/z-order :z-order/flying
                  :entity/rotation-angle (v2/angle-from-vector direction)
                  :entity/movement {:direction direction :speed speed}
                  :entity/image image
                  :entity/faction faction
                  :entity/delete-after-duration (/ max-range speed)
                  :entity/destroy-audiovisual :audiovisuals/hit-wall
                  :entity/projectile-collision {:entity-effects entity-effects
                                               :piercing? piercing?}}))

(defn- show-modal! [ctx {:keys [title text button-text on-click]}]
  (let [skin (:ctx/skin ctx)
        stage (:ctx/stage ctx)]
    (assert (not (group/find-actor (.getRoot ^Stage stage) "moon.ui.modal-window")))
    (.addActor ^Stage stage
                      (doto (window/create {:title title
                                            :skin skin
                                            :table/rows [[{:actor (Label. ^String text ^Skin skin)}]
                                                         [{:actor (doto (TextButton. button-text skin)
                                                                         (.addListener (proxy [ChangeListener] []
                                                                           (changed [_event _actor]
                                                                             (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (group/find-actor (.getRoot ^Stage stage)
                                                                                                "moon.ui.modal-window"))
                                                                             (on-click)))))}]]})
                        (window/set-modal! true)
                        (.setName "moon.ui.modal-window")
                        (.setPosition ^com.badlogic.gdx.scenes.scene2d.Actor (/ (viewport/get-world-width (.getViewport ^Stage stage)) 2) (float (* (viewport/get-world-height (.getViewport ^Stage stage)) (/ 3 4))) (float Align/center))))))

(defn- audiovisual! [ctx position audiovisual]
  (let [db (:ctx/db ctx)
        {:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (db/build db audiovisual)
                                             audiovisual)]
    (audio/play! (:ctx/audio ctx) sound)
    (spawn-effect! ctx position
                   {:entity/animation (assoc animation :delete-after-stopped? true)})))

(defn- handle-fsm-event! [ctx eid world-mouse-position event & [params]]
  (let [fsm (:entity/fsm @eid)
        _ (assert fsm)
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
            state-args (if params [new-state-k params] [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args eid (:ctx/elapsed-time ctx))]]
        (swap! eid assoc :entity/fsm new-fsm)
        (swap! eid assoc new-state-k (new-state-obj 1))
        (swap! eid dissoc old-state-k)
        (let [[state-k _state-v] old-state-obj]
          (case state-k
            :player-item-on-cursor
            (let [entity @eid
                  item (:entity/item-on-cursor entity)]
              (when item
                (swap! eid dissoc :entity/item-on-cursor)
                (audio/play! (:ctx/audio ctx) "bfxr_itemputground")
                (spawn-item! ctx (item-place-position (:entity/position entity)
                                                      world-mouse-position
                                                      (- (:entity/click-distance-tiles entity) 0.1))
                             item)))

            :player-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            :npc-sleeping
            (do (swap! eid add-text-effect (:ctx/elapsed-time ctx) "[WHITE]!" 1)
                (spawn-alert! ctx (:entity/position @eid) (:entity/faction @eid) 0.2))

            :npc-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            nil))
        (let [[state-k state-v] new-state-obj]
          (case state-k
            :player-item-on-cursor
            (let [{:keys [item]} state-v]
              (swap! eid assoc :entity/item-on-cursor item)
              nil)

            :active-skill
            (let [{:keys [skill]} state-v]
              (swap! eid update :entity/stats stats/pay-mana-cost (:skill/cost skill))
              (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
                     (timer/create (:ctx/elapsed-time ctx) (:skill/cooldown skill)))
              (audio/play! (:ctx/audio ctx) (:skill/start-action-sound skill))
              nil)

            :npc-dead
            (do (swap! eid assoc :entity/destroyed? true)
                nil)

            :player-moving
            (let [{:keys [movement-vector]} state-v]
              (swap! eid assoc :entity/movement {:direction movement-vector
                                                 :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                            0)})
              nil)

            :player-dead
            (do (audio/play! (:ctx/audio ctx) "bfxr_playerdeath")
                (show-modal! ctx {:title "YOU DIED - again!"
                                  :text "Good luck next time!"
                                  :button-text "OK"
                                  :on-click (fn [])})
                nil)

            :npc-moving
            (let [{:keys [movement-vector]} state-v]
              (swap! eid assoc :entity/movement {:direction movement-vector
                                                 :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                            0)})
              nil)

            nil))
        nil))))

(declare apply-effects!)

(defn handle-effect
  [[k v] effect-ctx ctx]
  (case k
    :effects/audiovisual
    (audiovisual! ctx (effect-ctx/get-target-position effect-ctx) v)

    :effects/projectile
    (let [source (effect-ctx/get-source effect-ctx)]
      (spawn-projectile! ctx
                         {:position (projectile-start-point @source
                                                            (effect-ctx/get-target-direction effect-ctx)
                                                            (:projectile/size v))
                          :direction (effect-ctx/get-target-direction effect-ctx)
                          :faction (:entity/faction @source)}
                         v))

    :effects/spawn
    (let [source (effect-ctx/get-source effect-ctx)]
      (spawn-creature! ctx {:position (effect-ctx/get-target-position effect-ctx)
                            :creature-property v
                            :components {:entity/fsm {:fsm :fsms/npc
                                                      :initial-state :npc-idle}
                                         :entity/faction (:entity/faction @source)}}))

    :effects/target-all
    (let [source (effect-ctx/get-source effect-ctx)
          active-entities (:ctx/active-entities ctx)
          colors (:ctx/colors ctx)
          raycaster (:ctx/raycaster ctx)
          source* @source]
      (doseq [target (affected-targets active-entities raycaster source*)]
        (spawn-line! ctx
                     {:start (:entity/position source*)
                      :end (:entity/position @target)
                      :duration 0.05
                      :color (:colors/target-all-line colors)
                      :thick? true})
        (apply-effects! ctx
                        {:effect/source source
                         :effect/target target}
                        (:entity-effects v))))

    :effects/target-entity
    (let [source (effect-ctx/get-source effect-ctx)
          target (effect-ctx/get-target effect-ctx)
          colors (:ctx/colors ctx)
          body        @source
          target-body @target
          {:keys [maxrange entity-effects]} v]
      (if (body/in-range? body target-body maxrange)
        (do (spawn-line! ctx
                         {:start (body/start-point body target-body)
                          :end (:entity/position target-body)
                          :duration 0.05
                          :color (:colors/target-entity-line colors)
                          :thick? true})
            (apply-effects! ctx effect-ctx entity-effects))
        (audiovisual! ctx
                      (body/end-point body target-body maxrange)
                      :audiovisuals/hit-ground)))

    :effects.target/audiovisual
    (audiovisual! ctx (:entity/position @(effect-ctx/get-target effect-ctx)) v)

    :effects.target/convert
    (let [source (effect-ctx/get-source effect-ctx)
          target (effect-ctx/get-target effect-ctx)]
      (swap! target assoc :entity/faction (:entity/faction @source))
      nil)

    :effects.target/damage
    (let [source (effect-ctx/get-source effect-ctx)
          target (effect-ctx/get-target effect-ctx)
          elapsed-time (:ctx/elapsed-time ctx)
          source* @source
          target* @target
          hp (stats/get-hitpoints (:entity/stats target*))]
      (cond
       (zero? (hp 0))
       nil

       ; TODO find a better way
       (not (:entity/stats target*))
       nil

       (and (:entity/stats source*)
            (:entity/stats target*)
            (< (rand) (stats/effective-armor-save (:entity/stats source*)
                                                  (:entity/stats target*))))
       (do (swap! target add-text-effect elapsed-time "[WHITE]ARMOR" 0.3)
           nil)

       :else
       (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                       (:damage/min-max (stats/calc-damage (:entity/stats source*)
                                                           (:entity/stats target*)
                                                           v))
                       (:damage/min-max v))
             dmg-amount (rand/int-between min-max)
             new-hp-val (max (- (hp 0) dmg-amount)
                             0)
             dmg-text (str "[RED]" dmg-amount "[]")]
         (swap! target assoc-in [:entity/stats :stats/hp 0] new-hp-val)
         (swap! target add-text-effect elapsed-time dmg-text 0.3)
         (handle-fsm-event! ctx target (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) (if (zero? new-hp-val) :kill :alert))
         (audiovisual! ctx (:entity/position target*) :audiovisuals/damage))))

    :effects.target/kill
    (handle-fsm-event! ctx (effect-ctx/get-target effect-ctx) (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :kill)

    :effects.target/melee-damage
    ; TODO AT EFFECT CREATION MAKE
    ; same @ applicable
    (handle-effect [:effects.target/damage (stats/melee-damage @(effect-ctx/get-source effect-ctx))]
                   effect-ctx
                   ctx)

    :effects.target/spiderweb
    (let [target (effect-ctx/get-target effect-ctx)]
      ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
      (when-not (:entity/temp-modifier @target)
        (swap! target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                   :counter (timer/create (:ctx/elapsed-time ctx) spiderweb-duration)})
        (swap! target update :entity/stats stats/add-mods spiderweb-modifiers)
        nil))

    :effects.target/stun
    (handle-fsm-event! ctx (effect-ctx/get-target effect-ctx) (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :stun v)))

(defn- apply-effects! [ctx effect-ctx effects]
  (doseq [effect (filter #(effect-applicable? % effect-ctx) effects)]
    (handle-effect effect effect-ctx ctx)))

(defn- toggle-inventory-visible! [ctx]
  (let [inventory (-> (.getRoot ^Stage (:ctx/stage ctx))
                      (group/find-actor "moon.ui.windows.inventory"))]
    (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory)))))

(defn- show-message! [ctx message]
  (-> (.getRoot ^Stage (:ctx/stage ctx))
      (#(group/find-actor % "player-message"))
      (.setUserObject (atom {:text message :counter 0}))))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (color/float-bits [1 1 0 0.5])
     :colors/mouseover-tile-none (color/float-bits [1 0 0 0.5])
     :colors/debug-body-outline-collides (color/float-bits [1 1 1 1])
     :colors/debug-body-outline (color/float-bits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (color/float-bits [1 0 0 1])
     :colors/debug-cell-entities (color/float-bits [1 0 0 0.6])
     :colors/debug-cell-occupied (color/float-bits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (color/float-bits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (color/float-bits [1 0 0 0.75])
     :colors/target-all-render (color/float-bits [1 0 0 0.5])
     :colors/target-entity-line (color/float-bits [1 0 0 0.75])
     :colors/target-entity-in-range (color/float-bits [1 0 0 0.5])
     :colors/target-entity-not-in-range (color/float-bits [1 1 0 0.5])
     :colors/enemy-color (color/float-bits [1 0 0 outline-alpha])
     :colors/friendly-color (color/float-bits [0 1 0 outline-alpha])
     :colors/neutral-color (color/float-bits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (color/float-bits [0 0.8 0 1])
                                :darkgreen (color/float-bits [0 0.5 0 1])
                                :yellow (color/float-bits [0.5 0.5 0 1])
                                :red (color/float-bits [0.5 0 0 1])})))
     :colors/hp-bar-rect (color/float-bits [0 0 0 1])
     :colors/temp-modifier (color/float-bits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (color/float-bits [1 1 1 0.125])
     :colors/active-skill-sector (color/float-bits [1 1 1 0.5])
     :colors/stunned (color/float-bits [1 1 1 0.6])
     :colors/explored-tile (color/float-bits [0.5 0.5 0.5 1])
     :colors/visible-tile (color/float-bits [1 1 1 1])
     :colors/invisible-tile (color/float-bits [0 0 0 1])
     :colors/droppable-item (color/float-bits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (color/float-bits [0.6 0 0 0.8 1])
     :colors/item-rect (color/float-bits [0.5 0.5 0.5 1])}))

(def controls
  {:zoom-in Input$Keys/MINUS
   :zoom-out Input$Keys/EQUALS
   :unpause-once Input$Keys/P
   :unpause-continously Input$Keys/SPACE
   :close-windows-key Input$Keys/ESCAPE
   :toggle-inventory Input$Keys/I
   :toggle-entity-info Input$Keys/E
   :open-debug-button Input$Buttons/RIGHT})

(def controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "rightclick on tile or entity - open debug data window"
             "Leftmouse click - use skill/drop item on cursor"]))

(def help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(defn- data-viewer-label-str [k]
  (str "[LIGHT_GRAY]:"
       (when-let [ns (namespace k)] (str ns "/"))
       "[][WHITE]"
       (name k)
       "[]"))

(defn- create-data-viewer-window
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [v->actor (fn [v skin]
                   (if (map? v)
                     (doto (TextButton. "Map" skin)
                       (.addListener (proxy [ChangeListener] []
                                            (changed [_event actor]
                                              (.addActor ^Stage (.getStage ^Actor actor)
                                                                (create-data-viewer-window
                                                                 {:title "title"
                                                                  :data v
                                                                  :width 500
                                                                  :height 500
                                                                  :skin skin}))))))
                     (Label. ^String (cond
                                  (or (keyword? v)
                                      (number? v)
                                      (boolean? v)
                                      (string? v))
                                  (str "[GOLD]" v "[]")

                                  :else
                                  (str (class v)))
                                ^Skin skin)))
        rows (for [[k v] (sort-by key data)]
               {:label (data-viewer-label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (table/create
                           {:table/rows (for [{:keys [label actor]} rows]
                                           [{:actor (Label. ^String label ^Skin skin)}
                                            {:actor actor}])})
        scroll-pane-cell {:actor (ScrollPane.
                                  ^Actor (table/create {:table/cell-defaults {:pad 1}
                                                 :table/rows [[scroll-pane-table]]})
                                  ^Skin skin)
                          :width width
                          :height 800}]
    (window/create {:title title
                    :skin skin
                    :table/rows [[scroll-pane-cell]]
                    :window/add-close-button? true})))

(def ctx-data-menu-item
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [ctx]
                        (let [skin (:ctx/skin ctx)
                              stage (:ctx/stage ctx)]
                        ; skin & stage
                        ; :moon.game/ui
                        ; moon.game.ui/data-viweer-window?
                        (.addActor ^Stage stage
                                        (create-data-viewer-window
                                         {:title "Data View"
                                          :data ctx
                                          :width 1000
                                          :height 1000
                                          :skin skin}))
                        ctx))}]})

(def debug-flags-menu-item
  {:label "Debug"
   :items [{:label "Toggle show-tile-grid?"
            :on-click #(update % :ctx/show-tile-grid? not)}
           {:label "Toggle show-cell-entities?"
            :on-click #(update % :ctx/show-cell-entities? not)}
           {:label "Toggle show-cell-occupied?"
            :on-click #(update % :ctx/show-cell-occupied? not)}
           {:label "Toggle show-body-bounds?"
            :on-click #(update % :ctx/show-body-bounds? not)}
           {:label "Potential field colors: off"
            :on-click #(assoc % :ctx/show-potential-field-colors? nil)}
           {:label "Potential field colors: :good"
            :on-click #(assoc % :ctx/show-potential-field-colors? :good)}
           {:label "Potential field colors: :evil"
            :on-click #(assoc % :ctx/show-potential-field-colors? :evil)}]})

(def select-world-menu-item
  {:label "Select World"
   :items (for [[label world-fn] [["Vampire" tmx/vampire]
                                  ["UF Caves" uf-caves/create]
                                  ["Modules" modules/create]]]
            {:label (str "Start " label)
             :on-click (fn [ctx]
                         #_(let [rebuild-actors! nil
                                 #_(fn rebuild-actors! [stage ctx]
                                     (.clear stage)
                                     ((requiring-resolve 'game.create.add-actors/step) ctx))
                                 create-world nil
                                 #_(requiring-resolve 'game.create.world/step)
                                 ui stage
                                 stage (:ctx/stage actor)]
                             (rebuild-actors! ui ctx)
                             #_(Disposable/.dispose (:ctx/tiled-map ctx))
                             (set! (.ctx ^Stage stage) (create-world ctx world-fn)))
                         ctx)})})

(def dev-menus
  [ctx-data-menu-item
   debug-flags-menu-item
   help-menu-item
   select-world-menu-item])

(def max-speed
  (/ minimum-size max-delta))

(def render-z-order
  (apply hash-map (interleave z-orders (range))))

(def ^:private active-skill-radius
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-fn-circle [ctx [x y] radius color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.circle shape-drawer x y radius)))

(defn- draw-fn-ellipse [ctx [x y] radius-x radius-y color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.ellipse shape-drawer x y radius-x radius-y)))

(defn- draw-fn-filled-circle [ctx [x y] radius color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.filledCircle shape-drawer (float x) (float y) (float radius))))

(defn- draw-fn-filled-rectangle [ctx x y w h color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.filledRectangle shape-drawer (float x) (float y) (float w) (float h))))

(defn- draw-fn-line [ctx [sx sy] [ex ey] color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.line shape-drawer (float sx) (float sy) (float ex) (float ey))))

(defn- draw-fn-grid [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (draw-fn-line ctx [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (draw-fn-line ctx [leftx liney] [rightx liney] color-float-bits))))

(defn- draw-fn-rectangle [ctx x y w h color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.rectangle shape-drawer x y w h)))

(defn- draw-fn-sector [ctx [center-x center-y] radius start-radians radians color-float-bits]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)]
    (.setColor shape-drawer (float color-float-bits))
    (.sector shape-drawer center-x center-y radius start-radians radians)))

(defn- draw-fn-text [ctx {:keys [font scale x y text up?]}]
  (let [font (or font (:ctx/default-font ctx))
        unit-scale (:ctx/unit-scale ctx)
        scale (or scale 1)
        font-data (.getData ^BitmapFont font)
        old-scale (.scaleX ^BitmapFont$BitmapFontData font-data)
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float scale))]
    (.setScale ^BitmapFont$BitmapFontData font-data (* old-scale scale))
    (.draw ^BitmapFont font
           ^Batch (:ctx/batch ctx)
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight ^BitmapFont font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (.setScale ^BitmapFont$BitmapFontData font-data old-scale)))

(defn- draw-fn-texture-region [ctx texture-region [x y] & {:keys [center? rotation]}]
  (let [unit-scale (:ctx/unit-scale ctx)
        [w h] (let [dimensions [(.getRegionWidth ^TextureRegion texture-region)
                                (.getRegionHeight ^TextureRegion texture-region)]]
                  (if (= @unit-scale 1)
                    dimensions
                    (mapv (comp float (partial * world-unit-scale))
                          dimensions)))]
    (if center?
      (Batch/.draw ^Batch (:ctx/batch ctx)
                   ^TextureRegion texture-region
                   (float (- (float x) (/ (float w) 2)))
                   (float (- (float y) (/ (float h) 2)))
                   (float (/ (float w) 2))
                   (float (/ (float h) 2))
                   (float w)
                   (float h)
                   (float 1)
                   (float 1)
                   (float (or rotation 0)))
      (.draw ^Batch (:ctx/batch ctx)
             ^TextureRegion texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))

(defn- draw-with-line-width! [ctx width draw-body]
  (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)
        old-line-width (.getDefaultLineWidth shape-drawer)]
    (.setDefaultLineWidth shape-drawer (* width old-line-width))
    (draw-body ctx)
    (.setDefaultLineWidth shape-drawer old-line-width)))

(defn effect-render
  [[k v] effect-ctx ctx]
  (case k
    :effects/target-all
    (let [source (effect-ctx/get-source effect-ctx)
          active-entities (:ctx/active-entities ctx)
          colors (:ctx/colors ctx)
          raycaster (:ctx/raycaster ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (draw-fn-line ctx
                      (:entity/position source*)
                      (:entity/position target*)
                      (:colors/target-all-render colors))))

    :effects/target-entity
    (when-let [target (effect-ctx/get-target effect-ctx)]
      (let [source (effect-ctx/get-source effect-ctx)
            colors (:ctx/colors ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (draw-fn-line ctx
                      (body/start-point body target-body)
                      (body/end-point body target-body maxrange)
                      (if (body/in-range? body target-body maxrange)
                        (:colors/target-entity-in-range colors)
                        (:colors/target-entity-not-in-range colors)))))

    nil))

(defn tile-color-setter*
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
              visible-tile-color))))))

(defn draw-component
  [ctx mouseover-actor world-mouse-position entity k v]
  (case k
    :entity/clickable
    (let [{:keys [text]} v
          {:keys [entity/position entity/height entity/mouseover?]} entity]
      (when (and mouseover? text)
        (let [[x y] position]
          (draw-fn-text ctx {:text text
                             :x x
                             :y (+ y (/ height 2))
                             :up? true}))))

    :player-item-on-cursor
    (let [{:keys [item]} v]
      (when-not mouseover-actor
        (draw-fn-texture-region ctx
                                (textures/texture-region (:ctx/textures ctx) (:entity/image item))
                                (item-place-position (:entity/position entity)
                                                     world-mouse-position
                                                     (- (:entity/click-distance-tiles entity) 0.1))
                                {:center? true})))

    :entity/animation
    (let [{:keys [frames cnt frame-duration]} v
          image (frames (min (int (/ (float cnt) (float frame-duration)))
                             (dec (count frames))))]
      (draw-fn-texture-region ctx
                              (textures/texture-region (:ctx/textures ctx) image)
                              (:entity/position entity)
                              {:center? true
                               :rotation (or (:entity/rotation-angle entity) 0)}))

    :entity/image
    (draw-fn-texture-region ctx
                            (textures/texture-region (:ctx/textures ctx) v)
                            (:entity/position entity)
                            {:center? true
                             :rotation (or (:entity/rotation-angle entity) 0)})

    :entity/line-render
    (let [{:keys [thick? end color]} v
          position (:entity/position entity)]
      (if thick?
        (draw-with-line-width! ctx 4 #(draw-fn-line % position end color))
        (draw-fn-line ctx position end color)))

    :entity/mouseover?
    (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
          colors (:ctx/colors ctx)
          player @(:ctx/player-eid ctx)
          color (cond (= faction (faction/enemy (:entity/faction player)))
                      (:colors/enemy-color colors)
                      (= faction (:entity/faction player))
                      (:colors/friendly-color colors)
                      :else
                      (:colors/neutral-color colors))]
      (draw-with-line-width! ctx 5
                             #(draw-fn-ellipse % position
                                                (/ width 2)
                                                (/ height 2)
                                                color)))

    :entity/stats
    (let [colors (:ctx/colors ctx)
          ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
      (when (or (< ratio 1) (:entity/mouseover? entity))
        (let [{:keys [entity/position entity/width entity/height]} entity
              [x y] position
              x (- x (/ width  2))
              y (+ y (/ height 2))
              height (* 5 world-unit-scale)
              border (* 1 world-unit-scale)]
          (draw-fn-filled-rectangle ctx x y width height (:colors/hp-bar-rect colors))
          (draw-fn-filled-rectangle ctx
                                    (+ x border)
                                    (+ y border)
                                    (- (* width ratio) (* 2 border))
                                    (- height (* 2 border))
                                    ((:colors/hp-bar colors) ratio)))))

    :entity/string-effect
    (let [{:keys [text]} v
          [x y] (:entity/position entity)]
      (draw-fn-text ctx {:text text
                         :x x
                         :y (+ y
                               (/ (:entity/height entity) 2)
                               (* 5 world-unit-scale))
                         :scale 2
                         :up? true}))

    :entity/temp-modifier
    (draw-fn-filled-circle ctx (:entity/position entity) 0.5 (:colors/temp-modifier (:ctx/colors ctx)))

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          colors (:ctx/colors ctx)
          textures (:ctx/textures ctx)
          elapsed-time (:ctx/elapsed-time ctx)
          {:keys [entity/image skill/effects]} skill
          radius active-skill-radius
          action-counter-ratio (timer/ratio elapsed-time counter)
          texture-region (textures/texture-region textures image)
          [x y] (:entity/position entity)
          y (+ (float y)
               (float (/ (:entity/height entity) 2))
               (float 0.15))
          center [x (+ y radius)]]
      (draw-fn-filled-circle ctx center radius (:colors/active-skill-circle colors))
      (draw-fn-sector ctx
                      center
                      radius
                      (math/to-radians 90)
                      (math/to-radians (* (float action-counter-ratio) 360))
                      (:colors/active-skill-sector colors))
      (draw-fn-texture-region ctx texture-region [(- (float x) radius) y])
      (doseq [effect effects]
        (effect-render effect effect-ctx ctx)))

    :npc-sleeping
    (let [{:keys [entity/position entity/height]} entity
          [x y] position]
      (draw-fn-text ctx {:text "zzz"
                         :x x
                         :y (+ y (/ height 2))
                         :up? true}))

    :stunned
    (draw-fn-circle ctx (:entity/position entity) 0.5 (:colors/stunned (:ctx/colors ctx)))))

(defn hp-mana-bar-create
  [ctx]
  (let [stage (:ctx/stage ctx)
        textures (:ctx/textures ctx)
        {:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (viewport/get-world-width (.getViewport ^Stage stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        draw-hpmana-bar! (fn [ctx x y content-file minmaxval name]
                           (draw-fn-texture-region ctx rahmen-tex-reg [x y])
                           (draw-fn-texture-region ctx
                                                   (textures/texture-region textures
                                                                            {:image/file content-file
                                                                             :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                                   [x y])
                           (draw-fn-text ctx {:text (str (number/readable (minmaxval 0))
                                                         "/"
                                                         (minmaxval 1)
                                                         " "
                                                         name)
                                              :x (+ x 75)
                                              :y (+ y 2)
                                              :up? true}))]
    (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
      (act [delta]
        (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
          (proxy-super act delta)))
      (draw [batch parent-alpha]
        (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
          (let [ctx (.ctx ^Stage stage)
                stats (:entity/stats @(:ctx/player-eid ctx))
                bar-x (- x (/ rahmenw 2))]
            (draw-hpmana-bar! ctx bar-x y-hp hpcontent-file (stats/get-hitpoints stats) "HP")
            (draw-hpmana-bar! ctx bar-x y-mana manacontent-file (stats/get-mana stats) "MP")))))))

(defn- clicked-inventory-cell-player-idle
  [ctx eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    (audio/play! (:ctx/audio ctx) "bfxr_takeit")
    (remove-item! ctx eid cell)
    (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :pickup-item item)))

(defn- clicked-inventory-cell-player-item-on-cursor
  [ctx eid cell]
  (let [entity @eid
        inventory (:entity/inventory entity)
        item-in-cell (get-in inventory cell)
        item-on-cursor (:entity/item-on-cursor entity)]
    (cond
     (and (not item-in-cell)
          (inventory/valid-slot? cell item-on-cursor))
     (do (swap! eid dissoc :entity/item-on-cursor)
         (audio/play! (:ctx/audio ctx) "bfxr_itemput")
         (set-item! ctx eid cell item-on-cursor)
         (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :dropped-item))

     (and item-in-cell
          (inventory/valid-slot? cell item-on-cursor))
     (do (swap! eid dissoc :entity/item-on-cursor)
         (audio/play! (:ctx/audio ctx) "bfxr_itemput")
         (remove-item! ctx eid cell)
         (set-item! ctx eid cell item-on-cursor)
         (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :dropped-item)
         (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :pickup-item item-in-cell)))))

(def k->clicked-inventory-cell
  {:player-item-on-cursor clicked-inventory-cell-player-item-on-cursor
   :player-idle clicked-inventory-cell-player-idle})

(defn handle-clicked-inventory-cell
  [ctx player-eid cell]
  (let [state-k (:state (:entity/fsm @player-eid))]
    (when-let [handler (k->clicked-inventory-cell state-k)]
      (handler ctx player-eid cell))))

(defn- inventory-window-cell [on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (Stack.)]
       (run! #(group/add-actor! stack %)
             [(proxy [Widget] []
                (draw [batch parent-alpha]
                  (when-let [stage (.getStage ^Actor this)]
                    (let [ctx (.ctx ^Stage stage)]
                      (draw-cell-rect! ctx
                                       @(:ctx/player-eid ctx)
                                       (.getX ^Actor this)
                                       (.getY ^Actor this)
                                       (let [[ux uy] (viewport/unproject (.getViewport ^Stage (:ctx/stage ctx))
                                                                         [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])
                                             local (.stageToLocalCoordinates ^Actor this
                                                                             (Vector2. (float ux) (float uy)))
                                             x (.x ^Vector2 local)
                                             y (.y ^Vector2 local)]
                                         (.hit ^Actor this (float x) (float y) true))
                                       (.getUserObject ^Actor (.getParent ^Actor this)))))))
              (doto (Image. ^Drawable background-drawable)
                (.setName "image-widget")
                (.setUserObject {:background-drawable background-drawable
                                      :cell-size cell-size}))])
       (doto stack
         (.addListener (proxy [ClickListener] []
                         (clicked [event _x _y]
                           (let [ctx (.ctx ^Stage (.getStage ^Event event))]
                             (on-click-cell ctx (:ctx/player-eid ctx) cell)))))
         (.setName "inventory-cell")
         (.setUserObject cell)))}))

(defn- inventory-window-build
  [{:keys [on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint ^Color (Color. 1 1 1 0.4))))
        ->cell (partial inventory-window-cell on-click-cell slot->drawable draw-cell-rect! cell-size)
        window (doto (window/create {:title "Inventory"
                                     :skin skin
                                     :table/rows [[{:actor (doto (table/create
                                                                  {:table/rows (concat [[nil nil
                                                                                        (->cell :inventory.slot/helm)
                                                                                        (->cell :inventory.slot/necklace)]
                                                                                       [nil
                                                                                        (->cell :inventory.slot/weapon)
                                                                                        (->cell :inventory.slot/chest)
                                                                                        (->cell :inventory.slot/cloak)
                                                                                        (->cell :inventory.slot/shield)]
                                                                                       [nil nil
                                                                                        (->cell :inventory.slot/leg)]
                                                                                       [nil
                                                                                        (->cell :inventory.slot/glove)
                                                                                        (->cell :inventory.slot/rings :position [0 0])
                                                                                        (->cell :inventory.slot/rings :position [1 0])
                                                                                        (->cell :inventory.slot/boot)]]
                                                                                      (for [y (range 4)]
                                                                                        (for [x (range 6)]
                                                                                          (->cell :inventory.slot/bag :position [x y]))))})
                                                                  (.setName "inventory-cell-table"))
                                                    :pad 4}]]})
                     (.setName "moon.ui.windows.inventory")
                     (.setVisible false))]
    (let [[x y] position]
      (.setPosition ^Actor window (float x) (float y)))
    window))

(defn inventory-window-create
  [ctx]
  (let [colors (:ctx/colors ctx)
        skin (:ctx/skin ctx)
        stage (:ctx/stage ctx)
        textures (:ctx/textures ctx)
        slot->y-sprite-idx #:inventory.slot {:weapon 0
                                             :shield 1
                                             :rings 2
                                             :necklace 3
                                             :helm 4
                                             :cloak 5
                                             :chest 6
                                             :leg 7
                                             :glove 8
                                             :boot 9
                                             :bag 10}
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (textures/texture-region textures
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))
        cell-size 48]
    (inventory-window-build
     {:on-click-cell handle-clicked-inventory-cell
      :draw-cell-rect! (fn [ctx player-entity x y mouseover? cell]
                         (draw-fn-rectangle ctx x y cell-size cell-size (:colors/item-rect colors))
                         (when (and mouseover?
                                    (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                           (let [item (:entity/item-on-cursor player-entity)
                                 color (if (inventory/valid-slot? cell item)
                                         (:colors/droppable-item colors)
                                         (:colors/not-allowed-drop-item colors))]
                             (draw-fn-filled-rectangle ctx (inc x) (inc y) (- cell-size 2) (- cell-size 2) color))))
      :skin skin
      :position [(viewport/get-world-width (.getViewport ^Stage stage))
                 (viewport/get-world-height (.getViewport ^Stage stage))]
      :slot->texture-region slot->texture-region
      :cell-size 48})))

(defn windows-create [ctx actor-fns]
  (let [group* (group/create)]
    (run! #(group/add-actor! group* %) (for [f actor-fns] (f ctx)))
    (doto group*
      (.setName "moon.ui.windows"))))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (Label. "MY LABEL TEXT" ^Skin skin)
        window (doto (window/create {:title title
                                     :skin skin
                                     :table/rows [[{:actor label :expand? true}]]})
                 (.setName actor-name)
                 (.setVisible visible?))]
    (let [[x y] position]
      (.setPosition ^Actor window (float x) (float y)))
    (group/add-actor! window (proxy [Actor] []
                               (act [delta]
                                 (when-let [stage (.getStage ^Actor this)]
                                   (.setText ^Label label ^String (set-label-text! (.ctx ^Stage stage))))
                                 (.pack ^Layout window)
                                 (let [^Actor this this]
                                   (proxy-super act delta)))
                               (draw [batch parent-alpha])))
    window))

(defn stage-info-window-create
  [ctx]
  (let [skin (:ctx/skin ctx)
        stage (:ctx/stage ctx)]
    (create-info-window
     {:title "Entity Info"
      :actor-name "moon.ui.windows.entity-info"
      :visible? false
      :position [(viewport/get-world-width (.getViewport ^Stage stage)) 0]
      :set-label-text! (fn [ctx]
                         (if-let [eid (:ctx/mouseover-eid ctx)]
                           (info-text (apply dissoc @eid [:entity/skills
                                                          :entity/faction
                                                          :active-skill])
                                      (:ctx/elapsed-time ctx))
                           ""))
      :skin skin})))

(defn entity-state-draw-ui-view
  [[k _v] eid ctx mouseover-actor ui-mouse-position]
  (case k
    :player-item-on-cursor
    (when mouseover-actor
      (draw-fn-texture-region ctx
                              (textures/texture-region (:ctx/textures ctx) (:entity/image (:entity/item-on-cursor @eid)))
                              ui-mouse-position
                              {:center? true}))

    nil))

(defn player-state-draw-create []
  (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
    (act [delta]
      (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)
            ctx (.ctx ^Stage stage)
            player-eid (:ctx/player-eid ctx)
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            ui-mouse-position (viewport/unproject (.getViewport ^Stage (:ctx/stage ctx))
                                                  [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])
            [x y] ui-mouse-position]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   ctx
                                   (mouseover-actor (:ctx/stage ctx) x y)
                                   ui-mouse-position)))))

(defn player-message-actor-create []
  (let [message-duration-seconds 0.5]
    (doto (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
            (act [delta]
              (let [state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil))))
              (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
                (proxy-super act delta)))
            (draw [batch parent-alpha]
              (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                (let [ctx (.ctx ^Stage stage)
                      state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)
                      vp-width (viewport/get-world-width (.getViewport ^Stage stage))
                      vp-height (viewport/get-world-height (.getViewport ^Stage stage))]
                  (when-let [text (:text @state)]
                    (draw-fn-text ctx {:x (/ vp-width 2)
                                       :y (+ (/ vp-height 2) 200)
                                       :text text
                                       :scale 2.5
                                       :up? true}))))))
      (.setName "player-message")
      (.setUserObject (atom nil)))))

(defn- interaction-state->txs [[k params] ctx player-eid world-mouse-position]
  (let [stage (:ctx/stage ctx)]
    (case k
      :interaction-state/mouseover-actor
      nil

      :interaction-state/clickable-mouseover-eid
      (let [{:keys [clicked-eid in-click-range?]} params]
        (if in-click-range?
          (case (:type (:entity/clickable @clicked-eid))
            :clickable/player
            (do (toggle-inventory-visible! ctx)
                nil)

            :clickable/item
            (let [item (:entity/item @clicked-eid)]
              (cond
                (-> (.getRoot ^Stage stage)
                    (group/find-actor "moon.ui.windows.inventory")
                    .isVisible)
                (do (swap! clicked-eid assoc :entity/destroyed? true)
                    (audio/play! (:ctx/audio ctx) "bfxr_takeit")
                    (handle-fsm-event! ctx player-eid world-mouse-position :pickup-item item))

                (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
                (do (swap! clicked-eid assoc :entity/destroyed? true)
                    (audio/play! (:ctx/audio ctx) "bfxr_pickup")
                    (pickup-item! ctx player-eid item)
                    nil)

                :else
                (do (audio/play! (:ctx/audio ctx) "bfxr_denied")
                    (show-message! ctx "Your Inventory is full")
                    nil))))
          (do (audio/play! (:ctx/audio ctx) "bfxr_denied")
              (show-message! ctx "Too far away")
              nil)))

      :interaction-state.skill/usable
      (let [[skill effect-ctx] params]
        (handle-fsm-event! ctx player-eid world-mouse-position :start-action [skill effect-ctx]))

      :interaction-state.skill/not-usable
      (let [state params]
        (do (audio/play! (:ctx/audio ctx) "bfxr_denied")
            (show-message! ctx (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
            nil))

      :interaction-state/no-skill-selected
      (do (audio/play! (:ctx/audio ctx) "bfxr_denied")
          (show-message! ctx "No selected skill")
          nil))))

(defn- handle-input
  [state-k eid ctx left-button-pressed? movement-vector mouseover-actor world-mouse-position]
  (case state-k
    :player-idle
    (if movement-vector
      (handle-fsm-event! ctx eid world-mouse-position :movement-input movement-vector)
      (when left-button-pressed?
        (interaction-state->txs (:ctx/interaction-state ctx)
                                ctx
                                eid
                                world-mouse-position)))

    :player-moving
    (if movement-vector
      (do (swap! eid assoc :entity/movement {:direction movement-vector
                                             :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                        0)})
          nil)
      (handle-fsm-event! ctx eid world-mouse-position :no-movement-input))

    :player-item-on-cursor
    (when (and left-button-pressed?
               (not mouseover-actor))
      (handle-fsm-event! ctx eid world-mouse-position :drop-item))

    nil))

(defn player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:entity/position @mouseover-eid))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v2/direction (:entity/position @player-eid)
                                         target-position)}))

(defn- choose-skill [ctx entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill-usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect-applicable? e effect-ctx)))
                          (some (fn [e] (effect-useful? e effect-ctx ctx))))))
       first))

(defn- create-effect-ctx
  [ctx eid]
  (let [world (:ctx/world ctx)
        raycaster (:ctx/raycaster ctx)
        entity @eid
        target (world/nearest-enemy world entity)
        target (when (and target
                          (raycaster/line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction entity
                                                @target))}))

(defn- update-effect-ctx
  [raycaster effect-ctx]
  (let [source (effect-ctx/get-source effect-ctx)
        target (effect-ctx/get-target effect-ctx)]
    (if (and target
             (not (:entity/destroyed? @target))
             (raycaster/line-of-sight? raycaster @source @target))
      effect-ctx
      (effect-ctx/without-target effect-ctx))))

(defn- tick-entity-animation
  [{:keys [delete-after-stopped?
           looping?
           cnt
           maxcnt]
    :as animation}
   eid
   ctx]
  (swap! eid assoc :entity/animation (let [maxcnt (float maxcnt)
                                           newcnt (+ (float cnt) (float (:ctx/delta-time ctx)))]
                                       (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                   looping? (min maxcnt (- newcnt maxcnt))
                                                                   :else maxcnt))))
  (when (and delete-after-stopped?
             (and (not looping?) (>= cnt maxcnt)))
    (swap! eid assoc :entity/destroyed? true))
  nil)

(defn- tick-entity-alert-friendlies-after-duration
  [{:keys [counter faction]}
   eid
   ctx]
  (when (timer/stopped? (:ctx/elapsed-time ctx) counter)
    (swap! eid assoc :entity/destroyed? true)
    (doseq [friendly-eid (->> {:position (:entity/position @eid)
                               :radius 4}
                              (world/circle->entities (:ctx/world ctx))
                              (filter #(= (:entity/faction @%) faction)))]
      (handle-fsm-event! ctx friendly-eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :alert)))
  nil)

(defn- tick-entity-string-effect
  [{:keys [counter]}
   eid
   ctx]
  (when (timer/stopped? (:ctx/elapsed-time ctx) counter)
    (swap! eid dissoc :entity/string-effect))
  nil)

(defn- tick-entity-skills
  [skills eid ctx]
  (doseq [{:keys [skill/cooling-down?] :as skill} (vals skills)
          :when (and cooling-down?
                     (timer/stopped? (:ctx/elapsed-time ctx) cooling-down?))]
    (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
  nil)

(defn- tick-entity-temp-modifier
  [{:keys [modifiers counter]}
   eid
   ctx]
  (when (timer/stopped? (:ctx/elapsed-time ctx) counter)
    (swap! eid dissoc :entity/temp-modifier)
    (swap! eid update :entity/stats stats/remove-mods modifiers))
  nil)

(defn- tick-entity-projectile-collision
  [{:keys [entity-effects already-hit-bodies piercing?]}
   eid
   ctx]
  (let [world (:ctx/world ctx)
        entity @eid
        hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                        (not= (:entity/faction entity)
                                              (:entity/faction @%))
                                        (:entity/collides? @%)
                                        (body/overlaps? entity
                                                        @%))
                                  (world/entities-at-touched-tiles world entity)))
        destroy? (or (and hit-entity (not piercing?))
                     (world/blocked-at-touched-tiles? world entity (:entity/z-order entity)))]
    (when hit-entity
      (swap! eid assoc-in [:entity/projectile-collision :already-hit-bodies]
             (conj already-hit-bodies hit-entity)))
    (when destroy?
      (swap! eid assoc :entity/destroyed? true))
    (when hit-entity
      (apply-effects! ctx
                      {:effect/source eid
                       :effect/target hit-entity}
                      entity-effects)))
  nil)

(defn- tick-active-skill
  [{:keys [skill effect-ctx counter]}
   eid
   ctx]
  (let [elapsed-time (:ctx/elapsed-time ctx)
        effect-ctx (update-effect-ctx (:ctx/raycaster ctx) effect-ctx)]
    (cond
     (not (seq (filter #(effect-applicable? % effect-ctx)
                       (:skill/effects skill))))
     (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :action-done)

     (timer/stopped? elapsed-time counter)
     (do (apply-effects! ctx effect-ctx (:skill/effects skill))
         (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :action-done)
         nil))))

(defn- tick-entity-delete-after-duration
  [counter eid ctx]
  (when (timer/stopped? (:ctx/elapsed-time ctx) counter)
    (swap! eid assoc :entity/destroyed? true))
  nil)

(defn- tick-stunned
  [{:keys [counter]} eid ctx]
  (when (timer/stopped? (:ctx/elapsed-time ctx) counter)
    (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :effect-wears-off)))

(defn- tick-npc-moving
  [{:keys [timer]} eid ctx]
  (when (timer/stopped? (:ctx/elapsed-time ctx) timer)
    (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :timer-finished)))

(defn- tick-npc-sleeping
  [_ eid ctx]
  (let [entity @eid]
    (when-let [distance (world/nearest-enemy-distance (:ctx/world ctx) entity)]
      (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
        (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :alert)))))

(defn- tick-npc-idle
  [_ eid ctx]
  (let [effect-ctx (create-effect-ctx ctx eid)]
    (if-let [skill (choose-skill ctx @eid effect-ctx)]
      (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :start-action [skill effect-ctx])
      (handle-fsm-event! ctx eid (viewport/unproject (:ctx/world-viewport ctx) [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)]) :movement-direction (or (world/find-direction (:ctx/world ctx) eid)
                                                         [0 0])))))

(defn- tick-entity-movement
  [{:keys [direction
           speed
           rotate-in-movement-direction?]
    :as movement}
   eid
   ctx]
  (assert (<= 0 speed (:ctx/max-speed ctx))
          (pr-str speed))
  (assert (vector? direction))
  (assert (or (zero? (v2/length direction))
              (number/nearly-equal? 1 (v2/length direction)))
          (str "cannot understand direction: " (pr-str direction)))
  (when-not (or (zero? (v2/length direction))
                (nil? speed)
                (zero? speed))
    (let [world (:ctx/world ctx)
          movement (assoc movement :delta-time (:ctx/delta-time ctx))
          body @eid]
      (when-let [body (if (:entity/collides? body)
                         (world/try-move-solid-body world body (:entity/id @eid) movement)
                         (update body :entity/position v2/move movement))]
        (swap! eid assoc :entity/position (:entity/position body))
        (when rotate-in-movement-direction?
          (swap! eid assoc :entity/rotation-angle
                 (v2/angle-from-vector direction)))
        (world/relocate-eid! (:ctx/world ctx) eid)
        nil))))

(def k->tick
  {:entity/animation tick-entity-animation
   :entity/alert-friendlies-after-duration tick-entity-alert-friendlies-after-duration
   :entity/string-effect tick-entity-string-effect
   :entity/skills tick-entity-skills
   :entity/temp-modifier tick-entity-temp-modifier
   :entity/projectile-collision tick-entity-projectile-collision
   :active-skill tick-active-skill
   :entity/delete-after-duration tick-entity-delete-after-duration
   :stunned tick-stunned
   :npc-moving tick-npc-moving
   :npc-sleeping tick-npc-sleeping
   :npc-idle tick-npc-idle
   :entity/movement tick-entity-movement})

(defn tick-component
  [ctx eid [k v]]
  (if-let [f (k->tick k)]
    (f v eid ctx)
    nil))

(defn- set-label-text-actor [label-widget text-fn]
  (proxy [Actor] []
    (act [delta]
      (when-let [stage (.getStage ^Actor this)]
        (.setText ^Label label-widget ^String (text-fn (.ctx ^Stage stage))))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha])))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (table/create {:table/rows [[{:actor (Image. ^Texture icon)}
                                                {:actor label}]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add-cell! table {:actor sub-table
                             :right? true
                             :expand-x? true})))
  ([skin table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add-cell! table {:actor label
                             :right? true
                             :expand-x? true}))))

(defn- dev-menu-main-table [skin menus update-labels]
  (let [table (table/create {:table/rows [(for [{:keys [label items]} menus]
                                            {:actor
                                             (doto (TextButton. label skin)
                                               (.addListener (proxy [ChangeListener] []
                                                              (changed [event actor]
                                                                (.addActor ^Stage (.getStage ^Event event)
                                                                                  (window/create {:title label
                                                                                                  :skin skin
                                                                                                  :table/rows [(for [{:keys [label on-click]} items]
                                                                                                                 {:actor
                                                                                                                  (doto (TextButton. label skin)
                                                                                                                    (.addListener (proxy [ChangeListener] []
                                                                                                                                   (changed [event actor]
                                                                                                                                     (let [stage (.getStage ^Event event)]
                                                                                                                                       (set! (.ctx ^Stage stage) (on-click (.ctx ^Stage stage))))))))})]
                                                                                                  :window/add-close-button? true}))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn- create-dev-menu
  [{:keys [menus update-labels skin]}]
  (doto (table/create {:table/rows [[{:actor (dev-menu-main-table skin menus update-labels)
                                                           :expand-x? true
                                                           :fill-x? true
                                                           :colspan 1}]
                                                         [{:actor (doto (Label. "" ^Skin skin)
                                                                        (.setTouchable Touchable/disabled))
                                                           :expand? true
                                                           :fill-x? true
                                                           :fill-y? true}]]})
        (.setFillParent true)))

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(defn- draw-tile-grid
  [ctx]
  (when (:ctx/show-tile-grid? ctx)
    (let [world-viewport (:ctx/world-viewport ctx)
          [left-x _right-x bottom-y _top-y] (orthographic-camera/frustum (viewport/get-camera world-viewport))]
      (draw-fn-grid ctx
                     (int left-x)
                     (int bottom-y)
                     (inc (int (viewport/get-world-width world-viewport)))
                     (+ 2 (int (viewport/get-world-height world-viewport)))
                     1
                     1
                     (color/float-bits [1 1 1 0.8])))))

(defn- draw-cell-debug
  [ctx]
  (let [world (:ctx/world ctx)
        colors (:ctx/colors ctx)
        world-viewport (:ctx/world-viewport ctx)
        tile-positions (orthographic-camera/visible-tiles (viewport/get-camera world-viewport))]
    (doseq [[[x y] cell*] (world/cells-at world tile-positions)]
      (when (and (:ctx/show-cell-entities? ctx) (seq (:entities cell*)))
        (draw-fn-filled-rectangle ctx x y 1 1 (:colors/debug-cell-entities colors)))
      (when (and (:ctx/show-cell-occupied? ctx) (seq (:occupied cell*)))
        (draw-fn-filled-rectangle ctx x y 1 1 (:colors/debug-cell-occupied colors)))
      (when-let [faction (:ctx/show-potential-field-colors? ctx)]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (draw-fn-filled-rectangle ctx x y 1 1 ((:colors/debug-potential-field colors) ratio)))))))))

(defn draw-entity-rectangle!
  [ctx entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (draw-fn-rectangle ctx x y width height color-float-bits)))

(defn- draw-entities!
  [ctx mouseover-actor world-mouse-position]
  (let [player-eid (:ctx/player-eid ctx)
        raycaster (:ctx/raycaster ctx)
        colors (:ctx/colors ctx)
        render-z-order (:ctx/render-z-order ctx)
        show-body-bounds? (:ctx/show-body-bounds? ctx)
        active-entities (:ctx/active-entities ctx)
        entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (coll/sort-by-order (group-by :entity/z-order entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try
        (do
          (when show-body-bounds?
            (draw-entity-rectangle! ctx
                                    entity
                                    (if (:entity/collides? entity)
                                      (:colors/debug-body-outline-collides colors)
                                      (:colors/debug-body-outline colors))))
          (doseq [[k v] entity
                  :when (get render-layer k)]
            (draw-component ctx mouseover-actor world-mouse-position entity k v)))
        (catch Throwable t
          (draw-entity-rectangle! ctx
                                  entity
                                  (:colors/debug-body-outline-render-error colors))
          (throwable/pretty-pst t))))))

(defn- highlight-mouseover-tile
  [ctx world-mouse-position]
  (let [colors (:ctx/colors ctx)
        world (:ctx/world ctx)
        [x y] (mapv int world-mouse-position)
        cell (world/cell-at world [x y])]
    (when (and cell (#{:air :none} (:movement cell)))
      (draw-fn-rectangle ctx x y 1 1
                         (case (:movement cell)
                           :air (:colors/mouseover-tile-air colors)
                           :none (:colors/mouseover-tile-none colors))))))

(defn- make-interaction-state
  [ctx mouseover-actor world-mouse-position]
  (let [player-eid (:ctx/player-eid ctx)
        mouseover-eid (:ctx/mouseover-eid ctx)
        stage (:ctx/stage ctx)]
    (cond
      mouseover-actor
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (v2/distance (:entity/position @player-eid)
                                        (:entity/position @mouseover-eid))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (-> (.getRoot ^Stage stage)
                            (#(group/find-actor % "moon.ui.action-bar"))
                            action-bar-selected-skill)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
              state (skill-usable-state skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn assoc-interaction-state [ctx mouseover-actor world-mouse-position]
  (assoc ctx :ctx/interaction-state (make-interaction-state ctx mouseover-actor world-mouse-position)))

(def k->cursor
  {:player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle (fn
                  [eid ctx]
                  (let [[k params] (:ctx/interaction-state ctx)]
                    (case k
                      :interaction-state/mouseover-actor
                      (let [[actor-type params] params
                            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                                           (let [inventory-slot params]
                                                             (get-in (:entity/inventory @eid) inventory-slot)))]
                        (cond
                         inventory-cell-with-item?
                         :cursors/hand-before-grab

                         (= actor-type :mouseover-actor/window-title-bar)
                         :cursors/move-window

                         (= actor-type :mouseover-actor/button)
                         :cursors/over-button

                         (= actor-type :mouseover-actor/unspecified)
                         :cursors/default

                         :else
                         :cursors/default))

                      :interaction-state/clickable-mouseover-eid
                      (let [{:keys [clicked-eid
                                    in-click-range?]} params]
                        (case (:type (:entity/clickable @clicked-eid))
                          :clickable/item (if in-click-range?
                                            :cursors/hand-before-grab
                                            :cursors/hand-before-grab-gray)
                          :clickable/player :cursors/bag))

                      :interaction-state.skill/usable
                      :cursors/use-skill

                      :interaction-state.skill/not-usable
                      :cursors/skill-not-usable

                      :interaction-state/no-skill-selected
                      :cursors/no-skill-selected)))})

(defn- update-time [ctx]
  (let [delta-ms (min (.getDeltaTime ^Graphics Gdx/graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- update-potential-fields
  [ctx]
  (doseq [[faction max-iterations] factions-iterations]
    (world/update-potential-fields! (:ctx/world ctx)
                  (:ctx/potential-field-cache ctx)
                  faction
                  (:ctx/active-entities ctx)
                  max-iterations))
  ctx)

(defn- tick-entities
  [ctx]
  (try
    (doseq [eid (:ctx/active-entities ctx)
            component @eid]
      (try (tick-component ctx eid component)
           (catch Throwable t
             (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
    (catch Throwable t
      (throwable/pretty-pst t)
      (.addActor ^Stage (:ctx/stage ctx)
                        (error-window/create
                         {:skin (:ctx/skin ctx)
                          :throwable t}))))
  ctx)

(def zoom-speed 0.025)

(defn update-draw-stage
  [ctx]
  (let [stage (:ctx/stage ctx)]
    (set! (.ctx ^Stage stage) ctx)
    (.act ^Stage stage)
    (.draw ^Stage stage)
    (.ctx ^Stage stage)))

(defn shape-drawer-texture []
  (let [pixmap (doto ^Pixmap (Pixmap. (int 1) (int 1) Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel (int 0) (int 0)))
        texture (Texture. ^TextureData (PixmapTextureData. ^Pixmap pixmap
                                                           ^Pixmap$Format (Pixmap/.getFormat ^Pixmap pixmap)
                                                           false
                                                           false))]
    (Disposable/.dispose pixmap)
    texture))

(def state (atom nil))

(defn create! [audio files input]
  (reset! state
          (as-> {:ctx/unit-scale (atom 1)
                 :ctx/active-entities nil
                 :ctx/delta-time nil
                 :ctx/mouseover-eid nil
                 :ctx/paused? false
                 :ctx/elapsed-time 0
                 :ctx/potential-field-cache (atom nil)
                 :ctx/show-potential-field-colors? nil
                 :ctx/show-cell-entities? false
                 :ctx/show-cell-occupied? false
                 :ctx/show-body-bounds? false
                 :ctx/show-tile-grid? false
                 :ctx/batch (SpriteBatch.)
                 :ctx/audio (audio/create audio files)
                 :ctx/shape-drawer-texture (shape-drawer-texture)}
            ctx
            (assoc ctx :ctx/shape-drawer
                   (ShapeDrawer. (:ctx/batch ctx)
                                 (TextureRegion. ^Texture (:ctx/shape-drawer-texture ctx) (int 1) (int 0) (int 1) (int 1))))
            (assoc ctx :ctx/skin
                   (let [skin (Skin. ^FileHandle (.internal ^Files files "skin/uiskin.json"))]
                     (set! (.markupEnabled ^BitmapFont$BitmapFontData
                                           (.getData (.getFont ^Skin skin "default-font")))
                           true)
                     skin))
            (let [stage* (Stage. (FitViewport. (float 1440) (float 900)) (:ctx/batch ctx))]
              (.setInputProcessor ^Input input ^InputProcessor stage*)
              (assoc ctx :ctx/stage stage*))
            (do
             (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0)
             (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1))
             ctx)
            (assoc ctx :ctx/cursors
                   (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                     (update-vals data
                                  (fn [[path-segment [hotspot-x hotspot-y]]]
                                    (let [path (format path-format path-segment)
                                          pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                                          cursor (.newCursor ^Graphics Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                                      (Disposable/.dispose pixmap*)
                                      cursor)))))
            (assoc ctx :ctx/textures
                   (textures/create files {:folder "resources/"
                                           :extensions #{"png" "bmp"}}))
            (assoc ctx :ctx/world-viewport
                   (let [world-width (* 1440 world-unit-scale)
                         world-height (* 900 world-unit-scale)]
                     (FitViewport. (float world-width)
                                   (float world-height)
                                   (doto (orthographic-camera/new)
                                     (orthographic-camera/set-to-ortho! false world-width world-height)))))
            (assoc ctx :ctx/default-font
                   (let [{:keys [path
                                 size
                                 quality-scaling
                                 use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                           :size 16
                                                           :quality-scaling 2
                                                           :use-integer-positions? false}
                         generator (FreeTypeFontGenerator. ^FileHandle (.internal ^Files files path))
                         parameter (let [p (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                     (set! (.size p) (* size quality-scaling))
                                     (set! (.minFilter p) Texture$TextureFilter/Linear)
                                     (set! (.magFilter p) Texture$TextureFilter/Linear)
                                     p)
                         font (.generateFont ^FreeTypeFontGenerator generator
                                             ^FreeTypeFontGenerator$FreeTypeFontParameter parameter)
                         font-data (.getData ^BitmapFont font)]
                     (Disposable/.dispose generator)
                     (.setScale ^BitmapFont$BitmapFontData font-data (/ quality-scaling))
                     (set! (.markupEnabled ^BitmapFont$BitmapFontData font-data) true)
                     (.setUseIntegerPositions ^BitmapFont font use-integer-positions?)
                     font))
            (merge (map->Record {}) ctx)
            (-> ctx
                (assoc :ctx/controls controls)
                (assoc :ctx/controls-info controls-info)
                (assoc :ctx/colors colors)
                (assoc :ctx/render-z-order render-z-order)
                (assoc :ctx/max-speed max-speed))
            (assoc ctx :ctx/db (db/create))
            (let [stage (:ctx/stage ctx)
                  skin (:ctx/skin ctx)
                  textures (:ctx/textures ctx)]
              (doseq [actor [(create-action-bar)
                             (create-dev-menu
                              {:menus dev-menus
                               :update-labels (for [item [{:label "elapsed-time"
                                                           :update-fn (fn [ctx]
                                                                        (str (number/readable (:ctx/elapsed-time ctx)) " seconds"))
                                                           :icon "images/clock.png"}
                                                          {:label "FPS"
                                                           :update-fn (fn [ctx] (.getFramesPerSecond ^Graphics Gdx/graphics))
                                                           :icon "images/fps.png"}
                                                          {:label "Mouseover-entity id"
                                                           :update-fn (fn [ctx]
                                                                        (when-let [entity (and (:ctx/mouseover-eid ctx) @(:ctx/mouseover-eid ctx))]
                                                                          (:entity/id entity)))
                                                           :icon "images/mouseover.png"}
                                                          {:label "paused?"
                                                           :update-fn :ctx/paused?}
                                                          {:label "GUI"
                                                           :update-fn (fn [ctx]
                                                                        (mapv int (viewport/unproject (.getViewport ^Stage (:ctx/stage ctx))
                                                                                                      [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])))}
                                                          {:label "World"
                                                           :update-fn (fn [ctx]
                                                                        (mapv int (viewport/unproject (:ctx/world-viewport ctx)
                                                                                                      [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])))}
                                                          {:label "Zoom"
                                                           :update-fn (fn [ctx]
                                                                        (orthographic-camera/zoom (viewport/get-camera (:ctx/world-viewport ctx))))
                                                           :icon "images/zoom.png"}]]
                                                (if (:icon item)
                                                  (update item :icon #(get textures %))
                                                  item))
                               :skin skin})
                             (hp-mana-bar-create ctx)
                             (windows-create ctx [stage-info-window-create
                                                  inventory-window-create])
                             (player-state-draw-create)
                             (player-message-actor-create)]]
                (.addActor ^Stage stage actor))
              ctx)
            (let [{:keys [tiled-map start-position]}
                  (level-fn {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                                         (db/all-raw (:ctx/db ctx) :properties/creatures)
                                                         #(textures/texture-region (:ctx/textures ctx) %))
                             :textures (:ctx/textures ctx)})]
              (assoc ctx
                     :ctx/tiled-map tiled-map
                     :ctx/start-position start-position))
            (assoc ctx :ctx/world (world/create (:ctx/tiled-map ctx)))
            (assoc ctx :ctx/explored-tile-corners
                   (atom (moon-g2d/create (moon-tiled-map/get-property (:ctx/tiled-map ctx) "width")
                                          (moon-tiled-map/get-property (:ctx/tiled-map ctx) "height")
                                          (constantly false))))
            (let [{:keys [width height cells]} (world/raycaster-data (:ctx/world ctx))
                  arr (make-array Boolean/TYPE width height)]
              (doseq [[[x y] blocked?] cells]
                (aset arr x y (boolean blocked?)))
              (assoc ctx :ctx/raycaster [arr width height]))
            (do
             (spawn-creature! ctx {:position (mapv (partial + 0.5) (:ctx/start-position ctx))
                                   :creature-property (db/build (:ctx/db ctx) :creatures/vampire)
                                   :components {:entity/fsm {:fsm :fsms/player
                                                             :initial-state :player-idle}
                                                :entity/faction :good
                                                :entity/player? true
                                                :entity/free-skill-points 3
                                                :entity/clickable {:type :clickable/player}
                                                :entity/click-distance-tiles 1.5}})
             ctx)
            (let [eid (world/entity-by-id (:ctx/world ctx) 1)]
              (assert (:entity/player? @eid))
              (assoc ctx :ctx/player-eid eid))
            (do
             (let [start-position (:ctx/start-position ctx)]
               (doseq [[position creature-id] (moon-tiled-map/spawn-positions (:ctx/tiled-map ctx))
                       :when (not= position start-position)]
                 (spawn-creature! ctx {:position (mapv (partial + 0.5) position)
                                       :creature-property (db/build (:ctx/db ctx) (keyword creature-id))
                                       :components {:entity/fsm {:fsm :fsms/npc
                                                                 :initial-state :npc-sleeping}
                                                    :entity/faction :evil}})))
             ctx))))

(defn dispose! []
  (let [ctx @state]
    (audio/dispose! (:ctx/audio ctx))
    (Disposable/.dispose (:ctx/batch ctx))
    (run! Disposable/.dispose (vals (:ctx/cursors ctx)))
    (Disposable/.dispose (:ctx/default-font ctx))
    (Disposable/.dispose (:ctx/shape-drawer-texture ctx))
    (Disposable/.dispose (:ctx/skin ctx))
    (run! Disposable/.dispose (vals (:ctx/textures ctx)))
    (Disposable/.dispose (:ctx/tiled-map ctx))))

(defn render! [mouse-position key-pressed? key-just-pressed? button-just-pressed?]
  (.glClearColor (.getGL20 ^Graphics Gdx/graphics) 0 0 0 0)
  (.glClear (.getGL20 ^Graphics Gdx/graphics) GL20/GL_COLOR_BUFFER_BIT)
  (swap! state #(or (.ctx ^Stage (:ctx/stage %)) %))
  (malli-schema/validate-humanize schema @state)
  (let [ui-mouse-position (viewport/unproject (.getViewport ^Stage (:ctx/stage @state)) mouse-position)
        world-mouse-position (viewport/unproject (:ctx/world-viewport @state) mouse-position)]
    (swap! state (fn [ctx]
                   (let [player-eid (:ctx/player-eid ctx)
                         raycaster (:ctx/raycaster ctx)
                         render-z-order (:ctx/render-z-order ctx)
                         mouseover-eid (:ctx/mouseover-eid ctx)
                         [x y] ui-mouse-position
                         new-eid (if (mouseover-actor (:ctx/stage ctx) x y)
                                   nil
                                   (let [player @player-eid
                                         hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                                      (world/point->entities (:ctx/world ctx) world-mouse-position))]
                                     (->> render-z-order
                                          (coll/sort-by-order hits #(:entity/z-order @%))
                                          reverse
                                          (filter #(raycaster/line-of-sight? raycaster player @%))
                                          first)))]
                     (when mouseover-eid
                       (swap! mouseover-eid dissoc :entity/mouseover?))
                     (when new-eid
                       (swap! new-eid assoc :entity/mouseover? true))
                     (assoc ctx :ctx/mouseover-eid new-eid))))
    (let [ctx @state]
      (when (button-just-pressed? (:open-debug-button (:ctx/controls ctx)))
        (let [world (:ctx/world ctx)
              mouseover-eid (:ctx/mouseover-eid ctx)
              data (or (and mouseover-eid @mouseover-eid)
                       (world/cell-at world (mapv int world-mouse-position)))]
          (.addActor ^Stage (:ctx/stage ctx)
                            (create-data-viewer-window
                             {:title "Data View"
                              :data data
                              :width 500
                              :height 500
                              :skin (:ctx/skin ctx)})))))
    (swap! state #(assoc % :ctx/active-entities
                         (world/active-entities (:ctx/world %) @(:ctx/player-eid %))))
    (orthographic-camera/set-position! (viewport/get-camera (:ctx/world-viewport @state))
                                       (:entity/position @(:ctx/player-eid @state)))
    (let [ctx @state
          raycaster (:ctx/raycaster ctx)
          world-viewport (:ctx/world-viewport ctx)
          colors (:ctx/colors ctx)
          explored-tile-corners (:ctx/explored-tile-corners ctx)
          tiled-map (:ctx/tiled-map ctx)]
      (moon-tiled-map/draw! tiled-map
                            (:ctx/batch ctx)
                            world-unit-scale
                            (viewport/get-camera world-viewport)
                            (tile-color-setter*
                             {:ray-blocked? (partial raycaster/blocked? raycaster)
                              :explored-tile-corners explored-tile-corners
                              :light-position (orthographic-camera/position (viewport/get-camera world-viewport))
                              :see-all-tiles? false
                              :explored-tile-color (:colors/explored-tile colors)
                              :visible-tile-color (:colors/visible-tile colors)
                              :invisible-tile-color (:colors/invisible-tile colors)})))
    (let [ctx @state
          world-viewport (:ctx/world-viewport ctx)
          unit-scale (:ctx/unit-scale ctx)
          [x y] ui-mouse-position
          mouseover-actor* (mouseover-actor (:ctx/stage ctx) x y)]
      (.setColor ^Batch (:ctx/batch ctx) (float 1) (float 1) (float 1) (float 1))
      (.setProjectionMatrix ^Batch (:ctx/batch ctx) (orthographic-camera/combined (viewport/get-camera world-viewport)))
      (.begin ^Batch (:ctx/batch ctx))
      (let [^ShapeDrawer shape-drawer (:ctx/shape-drawer ctx)
            old-line-width (.getDefaultLineWidth shape-drawer)]
        (.setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
        (reset! unit-scale world-unit-scale)
        (doseq [draw-fn [draw-tile-grid
                         draw-cell-debug
                         #(draw-entities! % mouseover-actor* world-mouse-position)
                         #(highlight-mouseover-tile % world-mouse-position)]]
          (draw-fn ctx))
        (reset! unit-scale 1)
        (.setDefaultLineWidth shape-drawer old-line-width))
      (.end ^Batch (:ctx/batch ctx))
      (swap! state assoc-interaction-state mouseover-actor* world-mouse-position)
      (let [ctx @state
            eid (:ctx/player-eid ctx)
            entity @eid
            state-k (:state (:entity/fsm entity))
            cursor-fn (k->cursor state-k)
            cursor-key (if (keyword? cursor-fn)
                         cursor-fn
                         (cursor-fn eid ctx))]
        (assert (contains? (:ctx/cursors ctx) cursor-key))
        (.setCursor ^Graphics Gdx/graphics ^Cursor (get (:ctx/cursors ctx) cursor-key)))
      (let [ctx @state
            eid (:ctx/player-eid ctx)
            entity @eid
            state-k (:state (:entity/fsm entity))
            movement-vector (let [r (when (key-pressed? Input$Keys/D) [1  0])
                                  l (when (key-pressed? Input$Keys/A) [-1 0])
                                  u (when (key-pressed? Input$Keys/W) [0  1])
                                  d (when (key-pressed? Input$Keys/S) [0 -1])]
                              (when (or r l u d)
                                (let [v (v2/normalise (reduce v2/add [0 0] (remove nil? [r l u d])))]
                                  (when (pos? (v2/length v))
                                    v))))]
        (handle-input state-k eid ctx
                      (button-just-pressed? Input$Buttons/LEFT)
                      movement-vector
                      mouseover-actor*
                      world-mouse-position)))
    (swap! state dissoc :ctx/interaction-state)
    (swap! state (fn [ctx]
                   (assoc ctx :ctx/paused?
                          (or #_error
                              (and pausing?
                                   (state->pause-game? (:state (:entity/fsm @(:ctx/player-eid ctx))))
                                   (not (or (key-just-pressed? (:unpause-once (:ctx/controls ctx)))
                                            (key-pressed? (:unpause-continously (:ctx/controls ctx))))))))))
    (swap! state (fn [ctx]
                   (if (:ctx/paused? ctx)
                     ctx
                     (-> ctx
                         update-time
                         update-potential-fields
                         tick-entities))))
    (let [ctx @state]
      (doseq [eid (world/destroyed-eids (:ctx/world ctx))]
        (world/unregister-eid! (:ctx/world ctx) eid)
        (doseq [[k v] @eid]
          (case k
            :entity/destroy-audiovisual
            (audiovisual! ctx (:entity/position @eid) v)
            nil))))
    (let [ctx @state
          stage (:ctx/stage ctx)
          world-viewport (:ctx/world-viewport ctx)]
      (when (key-pressed? (:zoom-in (:ctx/controls ctx)))
        (orthographic-camera/inc-zoom! (viewport/get-camera world-viewport) zoom-speed))
      (when (key-pressed? (:zoom-out (:ctx/controls ctx)))
        (orthographic-camera/inc-zoom! (viewport/get-camera world-viewport) (- zoom-speed)))
      (when (key-just-pressed? (:close-windows-key (:ctx/controls ctx)))
        (->> (group/find-actor (.getRoot ^Stage stage) "moon.ui.windows")
             group/get-children
             (run! #(.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor % false))))
      (when (key-just-pressed? (:toggle-inventory (:ctx/controls ctx)))
        (toggle-inventory-visible! ctx))
      (when (key-just-pressed? (:toggle-entity-info (:ctx/controls ctx)))
        (let [entity-info (group/find-actor (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
          (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info))))))
    (swap! state update-draw-stage)
    (malli-schema/validate-humanize schema @state)))

(defn resize! [width height]
  (let [ctx @state]
    (viewport/update! (.getViewport ^Stage (:ctx/stage ctx)) width height true)
    (viewport/update! (:ctx/world-viewport ctx) width height false)))

(def listener
  (reify ApplicationListener
    (create [_]
      (create! Gdx/audio Gdx/files Gdx/input))
    (dispose [_]
      (dispose!))
    (render [_]
      (let [input Gdx/input]
        (render! [(.getX ^Input input) (.getY ^Input input)]
                 #(.isKeyPressed ^Input input (int %))
                 #(.isKeyJustPressed ^Input input (int %))
                 #(.isButtonJustPressed ^Input input (int %)))))
    (resize [_ width height]
      (resize! width height))
    (pause [_])
    (resume [_])))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
