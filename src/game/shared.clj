(ns game.shared
  (:require [clojure.math :as math]
            [clojure.string :as str]
            [moon.camera :as orthographic-camera]
            [moon.color :as color]
            [moon.tiled-map :as moon-tiled-map]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.coll :as coll]
            [moon.db :as db]
            [moon.faction :as faction]
            [moon.g2d :as moon-g2d]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.m :as m]
            [moon.mods :as mods]
            [moon.world :as world]
            [moon.number :as number]
            [moon.rand :as rand]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.string :as string]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.v2 :as v2]
            [moon.val-max :as val-max]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx Audio Files Gdx Graphics Input Input$Buttons Input$Keys InputProcessor)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors Cursor GL20 OrthographicCamera Pixmap Pixmap$Format Texture Texture$TextureFilter TextureData)
           (com.badlogic.gdx.maps.tiled TiledMap)
           (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.graphics.glutils PixmapTextureData)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor Event Group Stage Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup Cell HorizontalGroup Image ImageButton Label ScrollPane Skin Stack Table TextButton TextTooltip TooltipManager Widget Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener ClickListener Drawable Layout TextureRegionDrawable)
           (com.badlogic.gdx.utils Align Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

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

(def db (db/create))
(def world (atom nil))
(def tiled-map (atom nil))
(def start-position (atom nil))
(def raycaster (atom nil))
(def player-eid (atom nil))
(def explored-tile-corners (atom nil))
(def potential-field-cache (atom nil))
(def active-entities (atom nil))
(def delta-time (atom nil))
(def mouseover-eid (atom nil))
(def elapsed-time (atom 0))
(def paused? (atom false))
(def show-potential-field-colors? (atom nil))
(def show-cell-entities? (atom false))
(def show-cell-occupied? (atom false))
(def show-body-bounds? (atom false))
(def show-tile-grid? (atom false))
(def interaction-state (atom nil))
(def audio (atom nil))
(def batch (atom nil))
(def unit-scale (atom 1))
(def cursors (atom nil))
(def default-font (atom nil))
(def world-viewport (atom nil))
(def shape-drawer (atom nil))
(def shape-drawer-texture (atom nil))
(def textures (atom nil))
(def skin (atom nil))
(def stage (atom nil))

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
    (:effect/target-position effect-ctx)

    :effects/projectile
    (:effect/target-direction effect-ctx)

    :effects/spawn
    (and (:entity/faction @(:effect/source effect-ctx))
         (:effect/target-position effect-ctx))

    :effects/target-all
    true

    :effects/target-entity
    (and (:effect/target effect-ctx)
         (seq (filter #(effect-applicable? % effect-ctx) (:entity-effects v))))

    :effects.target/audiovisual
    (:effect/target effect-ctx)

    :effects.target/convert
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)]
      (and target
           (= (:entity/faction @target)
              (faction/enemy (:entity/faction @source)))))

    :effects.target/damage
    (and (:effect/target effect-ctx)
         #_(:stats/hp @target))

    :effects.target/kill
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))

    :effects.target/melee-damage
    (effect-applicable? [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                        effect-ctx)

    :effects.target/spiderweb
    (:entity/stats @(:effect/target effect-ctx))

    :effects.target/stun
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))))

(defn effect-useful?
  [[k v] effect-ctx ray-blocked?]
  (case k
    :effects/audiovisual
    false

    :effects/projectile
    (let [{:keys [projectile/max-range] :as projectile} v
          source-p (:entity/position @(:effect/source effect-ctx))
          target-p (:entity/position @(:effect/target effect-ctx))]
      (and (not (let [[start1 target1 start2 target2] (v2/double-ray-endpositions source-p
                                                                                 target-p
                                                                                 (:projectile/size projectile))]
                  (or
                   (ray-blocked? start1 target1)
                   (ray-blocked? start2 target2))))
           (< (v2/distance source-p target-p)
              max-range)))

    :effects/target-all
    false

    :effects/target-entity
    (body/in-range? @(:effect/source effect-ctx)
                    @(:effect/target effect-ctx)
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

(defn create-action-bar []
  (let [table (doto (Table.)
                (#(.pad (.defaults ^Table %) (float 2))))]
    (doto (.add ^Table table ^Actor (doto (HorizontalGroup.)
                                      (.space (float 2))
                                      (.pad (float 2))
                                      (.setName "moon.ui.action-bar.horizontal-group")
                                      (.setUserObject (doto (ButtonGroup.)
                                                        (.setMaxCheckCount (int 1))
                                                        (.setMinCheckCount (int 0))))))
      (.expand)
      (.bottom))
    (.row ^Table table)
    (doto table
      (.pack)
      (.setFillParent true)
      (.setName "moon.ui.action-bar"))))

(defn- action-bar-get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")]
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
    (.addActor ^Group horizontal-group ^Actor button)
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
  (->> (.getChildren ^Group (.findActor ^Group inventory-window "inventory-cell-table"))
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn- inventory-window-remove-item! [inventory-window cell]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn- inventory-window-set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")
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

(defn- remove-item [entity cell]
  (let [item (get-in (:entity/inventory entity) cell)]
    (assert item)
    (cond-> (assoc-in entity (cons :entity/inventory cell) nil)
      (inventory/applies-modifiers? cell)
      (update :entity/stats stats/remove-mods (:stats/modifiers item)))))

(defn after-create-component
  [ui-set-skill! ui-set-item! elapsed-time eid [k v]]
  (case k
    :entity/fsm
    (let [{:keys [fsm initial-state]} v]
      (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
      (swap! eid assoc initial-state (create-entity-state [initial-state nil] eid elapsed-time))
      nil)

    :entity/skills
    (do
      (swap! eid assoc :entity/skills nil)
      (doseq [{:keys [property/id] :as skill} v]
        (assert (not (contains? (:entity/skills @eid) id)))
        (swap! eid update :entity/skills assoc id skill)
        (when (:entity/player? @eid)
          (ui-set-skill! skill)))
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
        (assert (item/valid? item))
        (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
          (assert cell)
          (assert (nil? cell-item))
          (swap! eid set-item cell item)
          (when (:entity/player? @eid)
            (ui-set-item! cell item))))
      nil)

    nil))

(defn- item-place-position [player-position world-mouse-position maxrange]
  (v2/add player-position
          (v2/scale (v2/direction player-position world-mouse-position)
                    (min maxrange
                         (v2/distance player-position world-mouse-position)))))

(defn mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn ui-mouse-position []
  (let [v2 (.unproject (.getViewport ^Stage @stage)
                       (Vector2. (float (.getX ^Input Gdx/input))
                                 (float (.getY ^Input Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn world-mouse-position []
  (let [v2 (.unproject ^Viewport @world-viewport
                       (Vector2. (float (.getX ^Input Gdx/input))
                                 (float (.getY ^Input Gdx/input))))]
    [(.x v2) (.y v2)]))

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

      (when (instance? Label actor)
        (when-let [p (.getParent ^Actor actor)]
          (when-let [p (.getParent ^Actor p)]
            (and (instance? Window p)
                 (= (.getTitleLabel ^Window p) actor)))))
      [:mouseover-actor/window-title-bar]

      (button? actor)
      [:mouseover-actor/button]

      :else
      [:mouseover-actor/unspecified])))

(defn spawn-creature [{:keys [position creature-property components]}]
  (assert creature-property)
  (-> creature-property
      (assoc :entity/position position
             :entity/collides? true
             :entity/z-order (if (:entity/flying? creature-property)
                               :z-order/flying
                               :z-order/ground))
      (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
      (m/safe-merge components)))

(defn- spawn-effect [position components]
  (assoc components
         :entity/width 0.5
         :entity/height 0.5
         :entity/z-order :z-order/effect
         :entity/position position))

(defn- spawn-alert [position faction duration elapsed-time]
  (spawn-effect position
                {:entity/alert-friendlies-after-duration
                 {:counter (timer/create elapsed-time duration)
                  :faction faction}}))

(defn- spawn-item [position item]
  {:entity/position position
   :entity/width 0.75
   :entity/height 0.75
   :entity/z-order :z-order/on-ground
   :entity/image (:entity/image item)
   :entity/item item
   :entity/clickable {:type :clickable/item
                      :text (:property/pretty-name item)}})

(defn- spawn-line [{:keys [start end duration color thick?]}]
  (spawn-effect start
                {:entity/line-render {:thick? thick? :end end :color color}
                 :entity/delete-after-duration duration}))

(defn- spawn-projectile
  [{:keys [position direction faction]}
   {:keys [entity/image
           projectile/max-range
           projectile/speed
           entity-effects
           projectile/size
           projectile/piercing?]}]
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
                                :piercing? piercing?}})

;; CTX SIDE EFFECT

; -> used in handle-fsm-event
(defn- show-modal! [^Skin skin ^Stage stage {:keys [title text button-text on-click]}]
  (assert (not (.findActor (.getRoot stage) "moon.ui.modal-window")))
  (let [^String title title
        ^String text text
        window (Window. title skin)]
    (.add window (Label. text skin))
    (.row window)
    (.add window (doto (TextButton. ^String button-text skin)
                   (.addListener (proxy [ChangeListener] []
                                   (changed [_event _actor]
                                     (.remove (.findActor (.getRoot stage)
                                                          "moon.ui.modal-window"))
                                     (on-click))))))
    (.row window)
    (.pack window)
    (.setModal window true)
    (.setName window "moon.ui.modal-window")
    (.setPosition window
                  (/ (.getWorldWidth (.getViewport stage)) 2)
                  (float (* (.getWorldHeight (.getViewport stage)) (/ 3 4)))
                  (float Align/center))
    (.addActor stage window)))

(defn ui-set-item! [ctx cell item]
  (let [skin @skin
        stage @stage
        textures @textures]
    (-> (.getRoot ^Stage stage)
        (.findActor "moon.ui.windows.inventory")
        (inventory-window-set-item! cell
                                    {:texture-region (textures/texture-region textures (:entity/image item))
                                     :tooltip-text (item/info-text item)}
                                    skin))))

(defn- ui-set-skill! [ctx elapsed-time skill]
  (let [skin @skin
        stage @stage
        textures @textures]
    (-> (.getRoot ^Stage stage)
        (.findActor "moon.ui.action-bar")
        (action-bar-add-skill! {:skill-id (:property/id skill)
                                :texture-region (textures/texture-region textures (:entity/image skill))
                                :tooltip-text (info-text skill elapsed-time)}
                               skin))))

(defn- play-sound! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (.play ^Sound (get sounds sound-name)))

(defn spawn-entity! [entity]
  (let [elapsed-time @elapsed-time
        entity (reduce (fn [m [k v]]
                         (assoc m k (create-component elapsed-time k v)))
                       {}
                       entity)
        entity (prepare-entity-geometry entity)
        eid (atom entity)]
    (world/register-eid! @world eid)
    (doseq [component @eid]
      (after-create-component #(ui-set-skill! nil elapsed-time %)
                              #(ui-set-item! nil %1 %2)
                              elapsed-time
                              eid
                              component))))

(defn audiovisual! [position audiovisual]
  (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (db/build db audiovisual)
                                             audiovisual)]
    (play-sound! @audio sound)
    (spawn-entity! (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

(defn handle-fsm-event! [eid world-mouse-position event & [params]]
  (let [fsm (:entity/fsm @eid)
        _ (assert fsm)
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
            state-args (if params [new-state-k params] [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args eid @elapsed-time)]]
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
                (play-sound! @audio "bfxr_itemputground")
                (spawn-entity! (spawn-item (item-place-position (:entity/position entity)
                                                                    world-mouse-position
                                                                    (- (:entity/click-distance-tiles entity) 0.1))
                                               item))))

            :player-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            :npc-sleeping
            (do (swap! eid add-text-effect @elapsed-time "[WHITE]!" 1)
                (spawn-entity! (spawn-alert (:entity/position @eid) (:entity/faction @eid) 0.2 @elapsed-time)))

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
                     (timer/create @elapsed-time (:skill/cooldown skill)))
              (play-sound! @audio (:skill/start-action-sound skill))
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
            (do (play-sound! @audio "bfxr_playerdeath")
                (show-modal! @skin @stage {:title "YOU DIED - again!"
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

; handle-fsm-event haengt an handle-effect
(defn handle-effect
  [[k v] effect-ctx world-mouse-position apply-effects!
   active-entities colors raycaster elapsed-time]
  (case k
    :effects/audiovisual
    (audiovisual! (:effect/target-position effect-ctx) v)

    :effects/projectile
    (let [source (:effect/source effect-ctx)]
      (spawn-entity! (spawn-projectile
                      {:position (projectile-start-point @source
                                                         (:effect/target-direction effect-ctx)
                                                         (:projectile/size v))
                       :direction (:effect/target-direction effect-ctx)
                       :faction (:entity/faction @source)}
                      v)))

    :effects/spawn
    (let [source (:effect/source effect-ctx)]
      (spawn-entity! (spawn-creature {:position (:effect/target-position effect-ctx)
                                      :creature-property v
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-idle}
                                                   :entity/faction (:entity/faction @source)}})))

    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target (affected-targets active-entities raycaster source*)]
        (spawn-entity! (spawn-line
                        {:start (:entity/position source*)
                         :end (:entity/position @target)
                         :duration 0.05
                         :color (:colors/target-all-line colors)
                         :thick? true}))
        (apply-effects! {:effect/source source
                         :effect/target target}
                        (:entity-effects v))))

    :effects/target-entity
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)
          body        @source
          target-body @target
          {:keys [maxrange entity-effects]} v]
      (if (body/in-range? body target-body maxrange)
        (do (spawn-entity! (spawn-line
                            {:start (body/start-point body target-body)
                             :end (:entity/position target-body)
                             :duration 0.05
                             :color (:colors/target-entity-line colors)
                             :thick? true}))
            (apply-effects! effect-ctx entity-effects))
        (audiovisual! (body/end-point body target-body maxrange)
                      :audiovisuals/hit-ground)))

    :effects.target/audiovisual
    (audiovisual! (:entity/position @(:effect/target effect-ctx)) v)

    :effects.target/convert
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)]
      (swap! target assoc :entity/faction (:entity/faction @source))
      nil)

    :effects.target/damage
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)
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
         (handle-fsm-event! target world-mouse-position (if (zero? new-hp-val) :kill :alert))
         (audiovisual! (:entity/position target*) :audiovisuals/damage))))

    :effects.target/kill
    (handle-fsm-event! (:effect/target effect-ctx) world-mouse-position :kill)

    :effects.target/melee-damage
    ; TODO AT EFFECT CREATION MAKE
    ; same @ applicable
    (handle-effect [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                   effect-ctx
                   world-mouse-position
                   apply-effects!
                   active-entities
                   colors
                   raycaster
                   elapsed-time)

    :effects.target/spiderweb
    (let [target (:effect/target effect-ctx)]
      ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
      (when-not (:entity/temp-modifier @target)
        (swap! target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                   :counter (timer/create elapsed-time spiderweb-duration)})
        (swap! target update :entity/stats stats/add-mods spiderweb-modifiers)
        nil))

    :effects.target/stun
    (handle-fsm-event! (:effect/target effect-ctx) world-mouse-position :stun v)))

(defn toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (.findActor "moon.ui.windows.inventory"))]
    (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory)))))

(defn- show-message! [stage message]
  (-> (.getRoot ^Stage stage)
      (.findActor "player-message")
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
   :toggle-entity-info Input$Keys/E})

(def controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "Leftmouse click - use skill/drop item on cursor"]))

(def help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(def debug-flags-menu-item
  {:label "Debug"
   :items [{:label "Toggle show-tile-grid?"
            :on-click #(swap! show-tile-grid? not)}
           {:label "Toggle show-cell-entities?"
            :on-click #(swap! show-cell-entities? not)}
           {:label "Toggle show-cell-occupied?"
            :on-click #(swap! show-cell-occupied? not)}
           {:label "Toggle show-body-bounds?"
            :on-click #(swap! show-body-bounds? not)}
           {:label "Potential field colors: off"
            :on-click #(reset! show-potential-field-colors? nil)}
           {:label "Potential field colors: :good"
            :on-click #(reset! show-potential-field-colors? :good)}
           {:label "Potential field colors: :evil"
            :on-click #(reset! show-potential-field-colors? :evil)}]})

(def select-world-menu-item
  {:label "Select World"
   :items (for [[label world-fn] [["Vampire" tmx/vampire]
                                  ["UF Caves" uf-caves/create]
                                  ["Modules" modules/create]]]
            {:label (str "Start " label)
             :on-click (fn []
                         #_(let [rebuild-actors! nil
                                 #_(fn rebuild-actors! [stage ctx]
                                     (.clear stage)
                                     ((requiring-resolve 'game.create.add-actors/step) ctx))
                                 create-world nil
                                 #_(requiring-resolve 'game.create.world/step)
                                 ui stage
                                 stage (:ctx/stage actor)]
                             (rebuild-actors! ui ctx)
                             #_(Disposable/.dispose @tiled-map)
                             (set! (.ctx ^Stage stage) (create-world ctx world-fn)))
                         nil)})})

(def dev-menus
  [debug-flags-menu-item
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

(defn- draw-fn-circle [shape-drawer [x y] radius color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.circle ^ShapeDrawer shape-drawer x y radius))

(defn- draw-fn-ellipse [shape-drawer [x y] radius-x radius-y color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.ellipse ^ShapeDrawer shape-drawer x y radius-x radius-y))

(defn- draw-fn-filled-circle [shape-drawer [x y] radius color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledCircle ^ShapeDrawer shape-drawer (float x) (float y) (float radius)))

(defn draw-fn-filled-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledRectangle ^ShapeDrawer shape-drawer (float x) (float y) (float w) (float h)))

(defn- draw-fn-line [shape-drawer [sx sy] [ex ey] color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.line ^ShapeDrawer shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn- draw-fn-grid [shape-drawer leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (draw-fn-line shape-drawer [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (draw-fn-line shape-drawer [leftx liney] [rightx liney] color-float-bits))))

(defn draw-fn-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.rectangle ^ShapeDrawer shape-drawer x y w h))

(defn- draw-fn-sector [shape-drawer [center-x center-y] radius start-radians radians color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.sector ^ShapeDrawer shape-drawer center-x center-y radius start-radians radians))

(defn- draw-fn-text [batch default-font unit-scale {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        scale (or scale 1)
        font-data (.getData ^BitmapFont font)
        old-scale (.scaleX ^BitmapFont$BitmapFontData font-data)
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float scale))]
    (.setScale ^BitmapFont$BitmapFontData font-data (* old-scale scale))
    (.draw ^BitmapFont font
           ^Batch batch
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

(defn- draw-fn-texture-region [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth ^TextureRegion texture-region)
                                (.getRegionHeight ^TextureRegion texture-region)]]
                  (if (= @unit-scale 1)
                    dimensions
                    (mapv (comp float (partial * world-unit-scale))
                          dimensions)))]
    (if center?
      (Batch/.draw ^Batch batch
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
      (.draw ^Batch batch
             ^TextureRegion texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))

(defn- draw-with-line-width! [shape-drawer width draw-body]
  (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
    (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* width old-line-width))
    (draw-body)
    (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width)))
(defn effect-render
  [[k v] effect-ctx shape-drawer active-entities colors raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (draw-fn-line shape-drawer
                      (:entity/position source*)
                      (:entity/position target*)
                      (:colors/target-all-render colors))))

    :effects/target-entity
    (when-let [target (:effect/target effect-ctx)]
      (let [source (:effect/source effect-ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (draw-fn-line shape-drawer
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
  [shape-drawer batch default-font unit-scale mouseover-actor world-mouse-position
   textures colors player elapsed-time active-entities raycaster
   entity k v]
  (case k
    :entity/clickable
    (let [{:keys [text]} v
          {:keys [entity/position entity/height entity/mouseover?]} entity]
      (when (and mouseover? text)
        (let [[x y] position]
          (draw-fn-text batch default-font unit-scale {:text text
                             :x x
                             :y (+ y (/ height 2))
                             :up? true}))))

    :player-item-on-cursor
    (let [{:keys [item]} v]
      (when-not mouseover-actor
        (draw-fn-texture-region batch unit-scale
                                (textures/texture-region textures (:entity/image item))
                                (item-place-position (:entity/position entity)
                                                     world-mouse-position
                                                     (- (:entity/click-distance-tiles entity) 0.1))
                                {:center? true})))

    :entity/animation
    (let [{:keys [frames cnt frame-duration]} v
          image (frames (min (int (/ (float cnt) (float frame-duration)))
                             (dec (count frames))))]
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region textures image)
                              (:entity/position entity)
                              {:center? true
                               :rotation (or (:entity/rotation-angle entity) 0)}))

    :entity/image
    (draw-fn-texture-region batch unit-scale
                            (textures/texture-region textures v)
                            (:entity/position entity)
                            {:center? true
                             :rotation (or (:entity/rotation-angle entity) 0)})

    :entity/line-render
    (let [{:keys [thick? end color]} v
          position (:entity/position entity)]
      (if thick?
        (draw-with-line-width! shape-drawer 4 #(draw-fn-line shape-drawer position end color))
        (draw-fn-line shape-drawer position end color)))

    :entity/mouseover?
    (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
          color (cond (= faction (faction/enemy (:entity/faction player)))
                      (:colors/enemy-color colors)
                      (= faction (:entity/faction player))
                      (:colors/friendly-color colors)
                      :else
                      (:colors/neutral-color colors))]
      (draw-with-line-width! shape-drawer 5
                             #(draw-fn-ellipse shape-drawer position
                                                (/ width 2)
                                                (/ height 2)
                                                color)))

    :entity/stats
    (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
      (when (or (< ratio 1) (:entity/mouseover? entity))
        (let [{:keys [entity/position entity/width entity/height]} entity
              [x y] position
              x (- x (/ width  2))
              y (+ y (/ height 2))
              height (* 5 world-unit-scale)
              border (* 1 world-unit-scale)]
          (draw-fn-filled-rectangle shape-drawer x y width height (:colors/hp-bar-rect colors))
          (draw-fn-filled-rectangle shape-drawer
                                    (+ x border)
                                    (+ y border)
                                    (- (* width ratio) (* 2 border))
                                    (- height (* 2 border))
                                    ((:colors/hp-bar colors) ratio)))))

    :entity/string-effect
    (let [{:keys [text]} v
          [x y] (:entity/position entity)]
      (draw-fn-text batch default-font unit-scale {:text text
                         :x x
                         :y (+ y
                               (/ (:entity/height entity) 2)
                               (* 5 world-unit-scale))
                         :scale 2
                         :up? true}))

    :entity/temp-modifier
    (draw-fn-filled-circle shape-drawer (:entity/position entity) 0.5 (:colors/temp-modifier colors))

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          {:keys [entity/image skill/effects]} skill
          radius active-skill-radius
          action-counter-ratio (timer/ratio elapsed-time counter)
          texture-region (textures/texture-region textures image)
          [x y] (:entity/position entity)
          y (+ (float y)
               (float (/ (:entity/height entity) 2))
               (float 0.15))
          center [x (+ y radius)]]
      (draw-fn-filled-circle shape-drawer center radius (:colors/active-skill-circle colors))
      (draw-fn-sector shape-drawer
                      center
                      radius
                      (math/to-radians 90)
                      (math/to-radians (* (float action-counter-ratio) 360))
                      (:colors/active-skill-sector colors))
      (draw-fn-texture-region batch unit-scale texture-region [(- (float x) radius) y])
      (doseq [effect effects]
        (effect-render effect effect-ctx shape-drawer
                       active-entities
                       colors
                       raycaster)))

    :npc-sleeping
    (let [{:keys [entity/position entity/height]} entity
          [x y] position]
      (draw-fn-text batch default-font unit-scale {:text "zzz"
                         :x x
                         :y (+ y (/ height 2))
                         :up? true}))

    :stunned
    (draw-fn-circle shape-drawer (:entity/position entity) 0.5 (:colors/stunned colors))))

(defn hp-mana-bar-create
  []
  (let [default-font @default-font
        stage @stage
        textures @textures
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
        [x y-mana] [(/ (.getWorldWidth (.getViewport ^Stage stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        draw-hpmana-bar! (fn [ctx batch x y content-file minmaxval name]
                           (draw-fn-texture-region batch unit-scale rahmen-tex-reg [x y])
                           (draw-fn-texture-region batch unit-scale
                                                   (textures/texture-region textures
                                                                            {:image/file content-file
                                                                             :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                                   [x y])
                           (draw-fn-text batch default-font unit-scale {:text (str (number/readable (minmaxval 0))
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
        (when (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)
          (let [stats (:entity/stats @@player-eid)
                bar-x (- x (/ rahmenw 2))]
            (draw-hpmana-bar! nil batch bar-x y-hp hpcontent-file (stats/get-hitpoints stats) "HP")
            (draw-hpmana-bar! nil batch bar-x y-mana manacontent-file (stats/get-mana stats) "MP")))))))

(defn ui-remove-item! [ctx cell]
  (-> (.getRoot ^Stage @stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-remove-item! cell)))

(defn handle-clicked-inventory-cell
  [player-eid audio ui-set-item! ui-remove-item! cell world-mouse-position]
  (case (:state (:entity/fsm @player-eid))
    :player-idle
    (when-let [item (get-in (:entity/inventory @player-eid) cell)]
      (play-sound! audio "bfxr_takeit")
      (swap! player-eid remove-item cell)
      (ui-remove-item! cell)
      (handle-fsm-event! player-eid world-mouse-position :pickup-item item))

    :player-item-on-cursor
    (let [entity @player-eid
          inventory (:entity/inventory entity)
          item-in-cell (get-in inventory cell)
          item-on-cursor (:entity/item-on-cursor entity)]
      (cond
       (and (not item-in-cell)
            (inventory/valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play-sound! audio "bfxr_itemput")
           (swap! player-eid set-item cell item-on-cursor)
           (ui-set-item! cell item-on-cursor)
           (handle-fsm-event! player-eid world-mouse-position :dropped-item))

       (and item-in-cell
            (inventory/valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play-sound! audio "bfxr_itemput")
           (swap! player-eid remove-item cell)
           (ui-remove-item! cell)
           (swap! player-eid set-item cell item-on-cursor)
           (ui-set-item! cell item-on-cursor)
           (handle-fsm-event! player-eid world-mouse-position :dropped-item)
           (handle-fsm-event! player-eid world-mouse-position :pickup-item item-in-cell))))

    nil))

(defn- inventory-window-cell [on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (Stack.)]
       (run! #(.addActor ^Group stack ^Actor %)
             [(proxy [Widget] []
                (draw [batch parent-alpha]
                  (when-let [stage (.getStage ^Actor this)]
                    (draw-cell-rect! @@player-eid
                                       (.getX ^Actor this)
                                       (.getY ^Actor this)
                                       (let [v2 (.unproject (.getViewport ^Stage stage)
                                                           (Vector2. (float (.getX ^Input Gdx/input))
                                                                     (float (.getY ^Input Gdx/input))))
                                             local (.stageToLocalCoordinates ^Actor this
                                                                             (Vector2. (.x v2) (.y v2)))
                                             x (.x ^Vector2 local)
                                             y (.y ^Vector2 local)]
                                         (.hit ^Actor this (float x) (float y) true))
                                       (.getUserObject ^Actor (.getParent ^Actor this))))))
              (doto (Image. ^Drawable background-drawable)
                (.setName "image-widget")
                (.setUserObject {:background-drawable background-drawable
                                      :cell-size cell-size}))])
       (doto stack
         (.addListener (proxy [ClickListener] []
                         (clicked [event _x _y]
                           (on-click-cell event cell))))
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
        cell-table (Table.)
        window (Window. "Inventory" ^Skin skin)]
    (doseq [row (concat [[{:actor nil} {:actor nil}
                         (->cell :inventory.slot/helm)
                         (->cell :inventory.slot/necklace)]
                        [{:actor nil}
                         (->cell :inventory.slot/weapon)
                         (->cell :inventory.slot/chest)
                         (->cell :inventory.slot/cloak)
                         (->cell :inventory.slot/shield)]
                        [{:actor nil} {:actor nil}
                         (->cell :inventory.slot/leg)]
                        [{:actor nil}
                         (->cell :inventory.slot/glove)
                         (->cell :inventory.slot/rings :position [0 0])
                         (->cell :inventory.slot/rings :position [1 0])
                         (->cell :inventory.slot/boot)]]
                       (for [y (range 4)]
                         (for [x (range 6)]
                           (->cell :inventory.slot/bag :position [x y]))))]
      (doseq [cell row]
        (.add cell-table ^Actor (:actor cell)))
      (.row cell-table))
    (.pack cell-table)
    (.setName cell-table "inventory-cell-table")
    (let [c (.add window cell-table)]
      (.pad c (float 4)))
    (.row window)
    (.pack window)
    (.setName window "moon.ui.windows.inventory")
    (.setVisible window false)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    window))

(defn inventory-window-create
  [on-click-cell draw-cell-rect!]
  (let [skin @skin
        stage @stage
        textures @textures
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
     {:on-click-cell on-click-cell
      :draw-cell-rect! draw-cell-rect!
      :skin skin
      :position [(.getWorldWidth (.getViewport ^Stage stage))
                 (.getWorldHeight (.getViewport ^Stage stage))]
      :slot->texture-region slot->texture-region
      :cell-size cell-size})))

(defn windows-create [actor-fns]
  (let [group* (Group.)]
    (run! #(.addActor ^Group group* ^Actor %) (for [f actor-fns] (f)))
    (doto group*
      (.setName "moon.ui.windows"))))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [^Skin skin skin
        label (Label. "MY LABEL TEXT" skin)
        window (Window. ^String title skin)
        c (.add window label)]
    (.expand c)
    (.row window)
    (.pack window)
    (.setName window actor-name)
    (.setVisible window visible?)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    (.addActor window (proxy [Actor] []
                         (act [delta]
                           (let [^Actor this this]
                             (when (.getStage this)
                               (.setText label ^String (set-label-text!)))
                             (.pack window)
                             (proxy-super act delta)))
                         (draw [batch parent-alpha])))
    window))

(defn stage-info-window-create
  []
  (let [skin @skin
        stage @stage]
    (create-info-window
     {:title "Entity Info"
      :actor-name "moon.ui.windows.entity-info"
      :visible? false
      :position [(.getWorldWidth (.getViewport ^Stage stage)) 0]
      :set-label-text! (fn []
                         (if-let [eid @mouseover-eid]
                           (info-text (apply dissoc @eid [:entity/skills
                                                          :entity/faction
                                                          :active-skill])
                                      @elapsed-time)
                           ""))
      :skin skin})))

(defn entity-state-draw-ui-view
  [[k _v] eid batch unit-scale mouseover-actor ui-mouse-position]
  (case k
    :player-item-on-cursor
    (when mouseover-actor
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region @textures (:entity/image (:entity/item-on-cursor @eid)))
                              ui-mouse-position
                              {:center? true}))

    nil))

(defn player-state-draw-create [unit-scale]
  (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
    (act [delta]
      (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)
            player-eid @player-eid
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            [x y] (ui-mouse-position)]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   batch
                                   unit-scale
                                   (mouseover-actor stage x y)
                                   (ui-mouse-position))))))

(defn player-message-actor-create [default-font unit-scale]
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
                (let [state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)
                      vp-width (.getWorldWidth (.getViewport ^Stage stage))
                      vp-height (.getWorldHeight (.getViewport ^Stage stage))]
                  (when-let [text (:text @state)]
                    (draw-fn-text batch default-font unit-scale {:x (/ vp-width 2)
                                       :y (+ (/ vp-height 2) 200)
                                       :text text
                                       :scale 2.5
                                       :up? true}))))))
      (.setName "player-message")
      (.setUserObject (atom nil)))))

(defn- interaction-state->txs [[k params] stage audio ui-set-item! player-eid world-mouse-position]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          (do (toggle-inventory-visible! stage)
              nil)

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (-> (.getRoot ^Stage stage)
                  (.findActor "moon.ui.windows.inventory")
                  .isVisible)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! audio "bfxr_takeit")
                  (handle-fsm-event! player-eid world-mouse-position :pickup-item item))

              (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! audio "bfxr_pickup")
                  (assert (item/valid? item))
                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @player-eid) item)]
                    (assert cell)
                    (assert (nil? cell-item))
                    (swap! player-eid set-item cell item)
                    (ui-set-item! cell item))
                  nil)

              :else
              (do (play-sound! audio "bfxr_denied")
                  (show-message! stage "Your Inventory is full")
                  nil))))
        (do (play-sound! audio "bfxr_denied")
            (show-message! stage "Too far away")
            nil)))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (handle-fsm-event! player-eid world-mouse-position :start-action [skill effect-ctx]))

    :interaction-state.skill/not-usable
    (let [state params]
      (do (play-sound! audio "bfxr_denied")
          (show-message! stage (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
          nil))

    :interaction-state/no-skill-selected
    (do (play-sound! audio "bfxr_denied")
        (show-message! stage "No selected skill")
        nil)))

(defn handle-input
  [state-k eid ctx audio left-button-pressed? movement-vector mouseover-actor world-mouse-position]
  (case state-k
    :player-idle
    (if movement-vector
      (handle-fsm-event! eid world-mouse-position :movement-input movement-vector)
      (when left-button-pressed?
        (interaction-state->txs @interaction-state
                                @stage
                                audio
                                #(ui-set-item! ctx %1 %2)
                                eid
                                world-mouse-position)))

    :player-moving
    (if movement-vector
      (do (swap! eid assoc :entity/movement {:direction movement-vector
                                             :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                        0)})
          nil)
      (handle-fsm-event! eid world-mouse-position :no-movement-input))

    :player-item-on-cursor
    (when (and left-button-pressed?
               (not mouseover-actor))
      (handle-fsm-event! eid world-mouse-position :drop-item))

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

(defn- choose-skill [ray-blocked? entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill-usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect-applicable? e effect-ctx)))
                          (some (fn [e] (effect-useful? e effect-ctx ray-blocked?))))))
       first))

(defn- create-effect-ctx
  [ctx eid]
  (let [world @world
        raycaster @raycaster
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
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)]
    (if (and target
             (not (:entity/destroyed? @target))
             (raycaster/line-of-sight? raycaster @source @target))
      effect-ctx
      (dissoc effect-ctx :effect/target))))

(defn tick-component
  [ctx world-mouse-position apply-effects! eid [k v]]
  (case k
    :entity/animation
    (let [{:keys [delete-after-stopped?
                  looping?
                  cnt
                  maxcnt]
           :as animation} v]
      (swap! eid assoc :entity/animation (let [maxcnt (float maxcnt)
                                               newcnt (+ (float cnt) (float @delta-time))]
                                           (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                       looping? (min maxcnt (- newcnt maxcnt))
                                                                       :else maxcnt))))
      (when (and delete-after-stopped?
                 (and (not looping?) (>= cnt maxcnt)))
        (swap! eid assoc :entity/destroyed? true))
      nil)

    :entity/alert-friendlies-after-duration
    (let [{:keys [counter faction]} v]
      (when (timer/stopped? @elapsed-time counter)
        (swap! eid assoc :entity/destroyed? true)
        (doseq [friendly-eid (->> {:position (:entity/position @eid)
                                   :radius 4}
                                  (world/circle->entities @world)
                                  (filter #(= (:entity/faction @%) faction)))]
          (handle-fsm-event! friendly-eid world-mouse-position :alert)))
      nil)

    :entity/string-effect
    (let [{:keys [counter]} v]
      (when (timer/stopped? @elapsed-time counter)
        (swap! eid dissoc :entity/string-effect))
      nil)

    :entity/skills
    (do (doseq [{:keys [skill/cooling-down?] :as skill} (vals v)
                :when (and cooling-down?
                           (timer/stopped? @elapsed-time cooling-down?))]
          (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
        nil)

    :entity/temp-modifier
    (let [{:keys [modifiers counter]} v]
      (when (timer/stopped? @elapsed-time counter)
        (swap! eid dissoc :entity/temp-modifier)
        (swap! eid update :entity/stats stats/remove-mods modifiers))
      nil)

    :entity/projectile-collision
    (let [{:keys [entity-effects already-hit-bodies piercing?]} v
          world @world
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
        (apply-effects! {:effect/source eid
                         :effect/target hit-entity}
                        entity-effects))
      nil)

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          elapsed-time @elapsed-time
          effect-ctx (update-effect-ctx @raycaster effect-ctx)]
      (cond
       (not (seq (filter #(effect-applicable? % effect-ctx)
                         (:skill/effects skill))))
       (handle-fsm-event! eid world-mouse-position :action-done)

       (timer/stopped? elapsed-time counter)
       (do (apply-effects! effect-ctx (:skill/effects skill))
           (handle-fsm-event! eid world-mouse-position :action-done)
           nil)))

    :entity/delete-after-duration
    (do (when (timer/stopped? @elapsed-time v)
          (swap! eid assoc :entity/destroyed? true))
        nil)

    :stunned
    (let [{:keys [counter]} v]
      (when (timer/stopped? @elapsed-time counter)
        (handle-fsm-event! eid world-mouse-position :effect-wears-off)))

    :npc-moving
    (let [{:keys [timer]} v]
      (when (timer/stopped? @elapsed-time timer)
        (handle-fsm-event! eid world-mouse-position :timer-finished)))

    :npc-sleeping
    (let [entity @eid]
      (when-let [distance (world/nearest-enemy-distance @world entity)]
        (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
          (handle-fsm-event! eid world-mouse-position :alert))))

    :npc-idle
    (let [effect-ctx (create-effect-ctx ctx eid)]
      (if-let [skill (choose-skill (partial raycaster/blocked? @raycaster) @eid effect-ctx)]
        (handle-fsm-event! eid world-mouse-position :start-action [skill effect-ctx])
        (handle-fsm-event! eid world-mouse-position :movement-direction (or (world/find-direction @world eid)
                                                           [0 0]))))

    :entity/movement
    (let [{:keys [direction
                  speed
                  rotate-in-movement-direction?]
           :as movement} v]
      (assert (<= 0 speed max-speed)
              (pr-str speed))
      (assert (vector? direction))
      (assert (or (zero? (v2/length direction))
                  (number/nearly-equal? 1 (v2/length direction)))
              (str "cannot understand direction: " (pr-str direction)))
      (when-not (or (zero? (v2/length direction))
                    (nil? speed)
                    (zero? speed))
        (let [world @world
              movement (assoc movement :delta-time @delta-time)
              body @eid]
          (when-let [body (if (:entity/collides? body)
                             (world/try-move-solid-body world body (:entity/id @eid) movement)
                             (update body :entity/position v2/move movement))]
            (swap! eid assoc :entity/position (:entity/position body))
            (when rotate-in-movement-direction?
              (swap! eid assoc :entity/rotation-angle
                     (v2/angle-from-vector direction)))
            (world/relocate-eid! world eid)
            nil))))

    nil))

(defn- set-label-text-actor [label-widget text-fn]
  (proxy [Actor] []
    (act [delta]
      (when (.getStage ^Actor this)
        (.setText ^Label label-widget ^String (text-fn)))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha])))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (Table.)]
     (doseq [cell [{:actor (Image. ^Texture icon)}
                   {:actor label}]]
       (.add ^Table sub-table ^Actor (:actor cell)))
     (.row ^Table sub-table)
     (.pack sub-table)
     (.addActor ^Group table (set-label-text-actor label text-fn))
     (doto (.add ^Table table ^Actor sub-table)
       (.right)
       (.expandX))))
  ([skin table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (.addActor ^Group table (set-label-text-actor label text-fn))
     (doto (.add ^Table table ^Actor label)
       (.right)
       (.expandX)))))

(defn- dev-menu-main-table [^Skin skin menus update-labels]
  (let [table (Table.)]
    (doseq [cell (for [{:keys [label items]} menus]
                   {:actor
                    (doto (TextButton. ^String label skin)
                      (.addListener (proxy [ChangeListener] []
                                      (changed [event actor]
                                        (let [^Stage stage (.getStage ^Event event)]
                                          (.addActor stage
                                                     (let [window (Window. ^String label skin)]
                                                       (doseq [{:keys [label on-click]} items]
                                                         (.add window (doto (TextButton. ^String label skin)
                                                                        (.addListener (proxy [ChangeListener] []
                                                                                        (changed [_event _actor]
                                                                                          (on-click)))))))
                                                       (.row window)
                                                       (.pack window)
                                                       (.add (.getTitleTable window)
                                                             (doto (TextButton. "X" skin)
                                                               (.addListener (proxy [ChangeListener] []
                                                                               (changed [_event _actor]
                                                                                 (.remove window))))))
                                                       window)))))))})]
      (.add table ^Actor (:actor cell)))
    (.row table)
    (.pack table)
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create-dev-menu
  [{:keys [menus update-labels skin]}]
  (let [table (Table.)]
    (doto (.add table ^Actor (dev-menu-main-table skin menus update-labels))
      (.expandX)
      (.fillX)
      (.colspan (int 1)))
    (.row table)
    (doto (.add table ^Actor (doto (Label. "" ^Skin skin)
                               (.setTouchable Touchable/disabled)))
      (.expand)
      (.fillX)
      (.fillY))
    (.row table)
    (doto table
      (.pack)
      (.setFillParent true))))

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

(defn draw-tile-grid
  [ctx shape-drawer ^Viewport world-viewport]
  (when @show-tile-grid?
    (let [[left-x _right-x bottom-y _top-y] (orthographic-camera/frustum (.getCamera world-viewport))]
      (draw-fn-grid shape-drawer
                     (int left-x)
                     (int bottom-y)
                     (inc (int (.getWorldWidth world-viewport)))
                     (+ 2 (int (.getWorldHeight world-viewport)))
                     1
                     1
                     (color/float-bits [1 1 1 0.8])))))

(defn draw-cell-debug
  [ctx shape-drawer ^Viewport world-viewport]
  (let [world @world
        tile-positions (orthographic-camera/visible-tiles (.getCamera world-viewport))]
    (doseq [[[x y] cell*] (world/cells-at world tile-positions)]
      (when (and @show-cell-entities? (seq (:entities cell*)))
        (draw-fn-filled-rectangle shape-drawer x y 1 1 (:colors/debug-cell-entities colors)))
      (when (and @show-cell-occupied? (seq (:occupied cell*)))
        (draw-fn-filled-rectangle shape-drawer x y 1 1 (:colors/debug-cell-occupied colors)))
      (when-let [faction @show-potential-field-colors?]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (draw-fn-filled-rectangle shape-drawer x y 1 1 ((:colors/debug-potential-field colors) ratio)))))))))

(defn draw-entity-rectangle!
  [ctx shape-drawer entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (draw-fn-rectangle shape-drawer x y width height color-float-bits)))

(defn draw-entities!
  [ctx shape-drawer batch default-font unit-scale mouseover-actor world-mouse-position]
  (let [player-eid @player-eid
        raycaster @raycaster
        textures @textures
        elapsed-time @elapsed-time
        show-body-bounds? @show-body-bounds?
        active-entities @active-entities
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
      (when show-body-bounds?
        (draw-entity-rectangle! ctx shape-drawer
                                entity
                                (if (:entity/collides? entity)
                                  (:colors/debug-body-outline-collides colors)
                                  (:colors/debug-body-outline colors))))
      (doseq [[k v] entity
              :when (get render-layer k)]
        (draw-component shape-drawer batch default-font unit-scale mouseover-actor world-mouse-position
                        textures colors player elapsed-time active-entities raycaster
                        entity k v)))))

(defn highlight-mouseover-tile
  [ctx shape-drawer world-mouse-position]
  (let [world @world
        [x y] (mapv int world-mouse-position)
        cell (world/cell-at world [x y])]
    (when (and cell (#{:air :none} (:movement cell)))
      (draw-fn-rectangle shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (:colors/mouseover-tile-air colors)
                           :none (:colors/mouseover-tile-none colors))))))

(defn- make-interaction-state
  [mouseover-actor world-mouse-position]
  (let [player-eid @player-eid
        mouseover-eid @mouseover-eid
        stage @stage]
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
                            (.findActor "moon.ui.action-bar")
                            action-bar-selected-skill)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
              state (skill-usable-state skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn assoc-interaction-state [mouseover-actor world-mouse-position]
  (reset! interaction-state (make-interaction-state mouseover-actor world-mouse-position)))

(def k->cursor
  {:player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle (fn
                  [eid]
                  (let [[k params] @interaction-state]
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

(defn update-time []
  (let [delta-ms (min (.getDeltaTime ^Graphics Gdx/graphics) max-delta)]
    (reset! delta-time delta-ms)
    (swap! elapsed-time + delta-ms)))

(defn update-potential-fields
  []
  (doseq [[faction max-iterations] factions-iterations]
    (world/update-potential-fields! @world
                  potential-field-cache
                  faction
                  @active-entities
                  max-iterations))
  nil)

(def zoom-speed 0.025)

(defn update-draw-stage []
  (let [stage @stage]
    (.act ^Stage stage)
    (.draw ^Stage stage)))
