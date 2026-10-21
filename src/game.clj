(ns game
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math :as math]
            [clojure.string :as str]
            [game.entity :refer [add-text-effect remove-item set-item]]
            [game.ui :as ui]
            [grid.find-direction :refer [find-direction]]
            [lib.animation :as animation]
            [lib.circle :as circle]
            [lib.color :as color]
            [lib.shape-drawer :as shape-drawer]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.faction :as faction]
            [moon.grid :as grid]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.number :as number]
            [moon.potential-field :as potential-field]
            [moon.raycaster :as raycaster]
            [moon.rectangle :as rectangle]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.tiled-map :as moon-tiled-map]
            [moon.tiled-map.draw :as tiled-map.draw]
            [moon.timer :as timer]
            [moon.v2 :as v2]
            [moon.val-max :as val-max]
            [moon.world :as world]
            [potential-field.update :as potential-field.update]
            [reduce-fsm :as fsm]
            [ui.action-bar :as action-bar]
            [ui.dev-menu :as dev-menu]
            [world.register-eid :refer [register-eid!]]
            [world.relocate-eid :refer [relocate-eid!]]
            [world.unregister-eid :refer [unregister-eid!]])
  (:import (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx ApplicationListener Files Gdx Graphics Input Input$Buttons Input$Keys InputProcessor)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color Colors Cursor OrthographicCamera Pixmap Pixmap$Format Texture Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData SpriteBatch TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.math Circle Intersector Rectangle Vector2 Vector3)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup HorizontalGroup Image Label Skin Stack Table TextButton TooltipManager Widget Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener ClickListener Drawable TextureRegionDrawable)
           (com.badlogic.gdx.utils Align Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport))
  (:gen-class))

(def ^:private controls
  {:zoom-in Input$Keys/MINUS
   :zoom-out Input$Keys/EQUALS
   :unpause-once Input$Keys/P
   :unpause-continously Input$Keys/SPACE
   :close-windows-key Input$Keys/ESCAPE
   :toggle-inventory Input$Keys/I
   :toggle-entity-info Input$Keys/E})

(defn mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn ui-mouse-position [stage]
  (let [v2 (.unproject (.getViewport ^Stage stage)
                       (Vector2. (float (.getX ^Input Gdx/input))
                                 (float (.getY ^Input Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn world-mouse-position [world-viewport]
  (let [v2 (.unproject ^Viewport world-viewport
                       (Vector2. (float (.getX ^Input Gdx/input))
                                 (float (.getY ^Input Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn- button?
  [actor]
  (let [button-class? (fn [a] (some #(= Button %) (supers (class a))))]
    (or (button-class? actor)
        (when-let [parent (.getParent ^Actor actor)]
          (button-class? parent)))))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (.getParent ^Actor actor)
                            (= "inventory-cell" (.getName ^Actor (.getParent ^Actor actor)))
                            (.getUserObject ^Actor (.getParent ^Actor actor)))]
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

(defn- tile-color-setter*
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
            explored? (get-in @explored-tile-corners position) ; TODO needs int call ?
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
                (swap! explored-tile-corners assoc-in (mapv int position) true))
              visible-tile-color))))))

(defn- white-pixel-texture []
  (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel 0 0))
        texture (Texture. pixmap)]
    (.dispose pixmap)
    texture))

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
                    (mapv (comp float (partial * @unit-scale))
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



(defn- item-place-position [player-position world-mouse-position maxrange]
  (mapv + player-position
        (v2/scale (v2/direction player-position world-mouse-position)
                  (min maxrange
                       (v2/distance player-position world-mouse-position)))))

(defn- spawn-creature [{:keys [position creature-property components]}]
  (assert creature-property)
  (let [entity (-> creature-property
                   (assoc :entity/position position
                          :entity/collides? true
                          :entity/z-order (if (:entity/flying? creature-property)
                                            :z-order/flying
                                            :z-order/ground))
                   (assoc :entity/destroy-audiovisual :audiovisuals/creature-die))]
    (assert (not-any? #(contains? entity %) (keys components)))
    (merge entity components)))

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
   :entity/rotation-angle (let [angle (math/to-degrees
                                       (math/atan2 (v2/crs [0 1] direction)
                                                   (v2/dot [0 1] direction)))]
                            (if (neg? angle)
                              (+ angle 360)
                              angle))
   :entity/movement {:direction direction :speed speed}
   :entity/image image
   :entity/faction faction
   :entity/delete-after-duration (/ max-range speed)
   :entity/destroy-audiovisual :audiovisuals/hit-wall
   :entity/projectile-collision {:entity-effects entity-effects
                                 :piercing? piercing?}})

(defn- create-entity-state [[k v] eid elapsed-time]
  (case k
    :active-skill
    (let [[skill effect-ctx] v]
      {:skill skill
       :effect-ctx effect-ctx
       :counter (timer/create elapsed-time
                              (/ (:skill/action-time skill)
                                 (or (stats/get-value (:entity/stats @eid)
                                                      (:skill/action-time-modifier-key skill))
                                     1)))})

    :stunned
    {:counter (timer/create elapsed-time v)}

    :player-moving
    {:movement-vector v}

    :npc-moving
    {:movement-vector v
     :timer (timer/create elapsed-time
                          (* (stats/get-value (:entity/stats @eid) :stats/reaction-time)
                             0.016))}

    :player-item-on-cursor
    {:item v}

    v))

(def ^:private fsms
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

(defn- after-create-component
  [elapsed-time eid [k v]]
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
        (swap! eid update :entity/skills assoc id skill))
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
                                                        [slot (mapv (fn [x] (mapv (fn [y] nil)
                                                                                  (range height)))
                                                                    (range width))]))
                                                 (into {})))
      (doseq [item v]
        (assert (item/valid? item))
        (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
          (assert cell)
          (assert (nil? cell-item))
          (swap! eid set-item cell item)))
      nil)

    nil))

(defn- spawn-entity! [world entity]
  (let [{:world/keys [elapsed-time z-orders minimum-size]} world
        elapsed-time* @elapsed-time
        entity (reduce (fn [m [k v]]
                         (assoc m k (case k
                                      :entity/animation
                                      (animation/create v)

                                      :entity/delete-after-duration
                                      (timer/create elapsed-time* v)

                                      :entity/projectile-collision
                                      (assoc v :already-hit-bodies #{})

                                      :entity/stats
                                      (-> v
                                          (update :stats/mana (fn [v] [v v]))
                                          (update :stats/hp   (fn [v] [v v])))

                                      v)))
                       {}
                       entity)
        entity (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
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
                        :entity/rotation-angle (or rotation-angle 0)))
        eid (atom entity)]
    (register-eid! world eid)
    (doseq [component @eid]
      (after-create-component elapsed-time* eid component))
    eid))

(defn- audiovisual! [db schemas world play-sound! position audiovisual]
  (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (db/build db schemas audiovisual)
                                             audiovisual)]
    (play-sound! sound)
    (spawn-entity! @world
                   (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

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

(defn- handle-fsm-event! [world play-sound! skin stage textures eid world-mouse-position event & [params]]
  (let [elapsed-time* @(:world/elapsed-time @world)
        fsm (:entity/fsm @eid)
        _ (assert fsm)
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
            state-args (if params [new-state-k params] [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args eid elapsed-time*)]]
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
                (play-sound! "bfxr_itemputground.wav")
                (spawn-entity! @world
                               (spawn-item (item-place-position (:entity/position entity)
                                                                world-mouse-position
                                                                (- (:entity/click-distance-tiles entity) 0.1))
                                           item))))

            :player-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            :npc-sleeping
            (do (swap! eid add-text-effect elapsed-time* "[WHITE]!" 1)
                (spawn-entity! @world
                               (spawn-alert (:entity/position @eid) (:entity/faction @eid) 0.2 elapsed-time*)))

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
            (let [{:keys [skill]} state-v
                  cost (:skill/cost skill)]
              (swap! eid update :entity/stats
                     (fn [stats]
                       (let [mana-val ((stats/get-mana stats) 0)]
                         (assert (<= cost mana-val))
                         (assoc-in stats [:stats/mana 0] (- mana-val cost)))))
              (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
                     (timer/create elapsed-time* (:skill/cooldown skill)))
              (play-sound! (:skill/start-action-sound skill))
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
            (do (play-sound! "bfxr_playerdeath.wav")
                (show-modal! skin stage {:title "YOU DIED - again!"
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

(defn- effect-applicable?
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

(defn- double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (v2/direction [start-x start-y]
                        [target-y target-y])
        [normal1 normal2] (v2/normal-vectors v)
        normal1 (v2/scale normal1 (/ path-w 2))
        normal2 (v2/scale normal2 (/ path-w 2))
        start1  (mapv + [start-x  start-y]  normal1)
        start2  (mapv + [start-x  start-y]  normal2)
        target1 (mapv + [target-x target-y] normal1)
        target2 (mapv + [target-x target-y] normal2)]
    [start1 target1 start2 target2]))

(defn- effect-useful?
  [[k v] effect-ctx ray-blocked?]
  (case k
    :effects/audiovisual
    false

    :effects/projectile
    (let [{:keys [projectile/max-range] :as projectile} v
          source-p (:entity/position @(:effect/source effect-ctx))
          target-p (:entity/position @(:effect/target effect-ctx))]
      (and (not (let [[start1 target1 start2 target2] (double-ray-endpositions source-p
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

(defn- affected-targets
  [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defn- handle-effect!
  [db schemas world play-sound! skin stage textures
   [k v] effect-ctx world-mouse-position apply-effects!
   active-entities raycaster]
  (let [elapsed-time* @(:world/elapsed-time @world)]
    (case k
      :effects/audiovisual
      (audiovisual! db schemas world play-sound! (:effect/target-position effect-ctx) v)

      :effects/projectile
      (let [source (:effect/source effect-ctx)
            source* @source
            direction (:effect/target-direction effect-ctx)
            size (:projectile/size v)]
        (spawn-entity! @world
                       (spawn-projectile
                        {:position (mapv + (:entity/position source*)
                                         (v2/scale direction
                                                   (+ (/ (:entity/width source*) 2) size 0.1)))
                         :direction direction
                         :faction (:entity/faction source*)}
                        v)))

      :effects/spawn
      (let [source (:effect/source effect-ctx)]
        (spawn-entity! @world
                       (spawn-creature {:position (:effect/target-position effect-ctx)
                                        :creature-property v
                                        :components {:entity/fsm {:fsm :fsms/npc
                                                                  :initial-state :npc-idle}
                                                     :entity/faction (:entity/faction @source)}})))

      :effects/target-all
      (let [source (:effect/source effect-ctx)
            source* @source]
        (doseq [target (affected-targets active-entities raycaster source*)]
          (spawn-entity! @world
                         (spawn-line
                          {:start (:entity/position source*)
                           :end (:entity/position @target)
                           :duration 0.05
                           :color (color/float-bits 1 0 0 0.75)
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
          (do (spawn-entity! @world
                             (spawn-line
                              {:start (body/start-point body target-body)
                               :end (:entity/position target-body)
                               :duration 0.05
                               :color (color/float-bits 1 0 0 0.75)
                               :thick? true}))
              (apply-effects! effect-ctx entity-effects))
          (audiovisual! db schemas world play-sound!
                        (body/end-point body target-body maxrange)
                        :audiovisuals/hit-ground)))

      :effects.target/audiovisual
      (audiovisual! db schemas world play-sound! (:entity/position @(:effect/target effect-ctx)) v)

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
         (do (swap! target add-text-effect elapsed-time* "[WHITE]ARMOR" 0.3)
             nil)

         :else
         (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                         (:damage/min-max (stats/calc-damage (:entity/stats source*)
                                                             (:entity/stats target*)
                                                             v))
                         (:damage/min-max v))
               dmg-amount (let [[lower upper] min-max]
                            (+ lower (rand-int (inc (- upper lower)))))

               new-hp-val (max (- (hp 0) dmg-amount)
                               0)
               dmg-text (str "[RED]" dmg-amount "[]")]
           (swap! target assoc-in [:entity/stats :stats/hp 0] new-hp-val)
           (swap! target add-text-effect elapsed-time* dmg-text 0.3)
           (handle-fsm-event! world play-sound! skin stage textures target world-mouse-position (if (zero? new-hp-val) :kill :alert))
           (audiovisual! db schemas world play-sound! (:entity/position target*) :audiovisuals/damage))))

      :effects.target/kill
      (handle-fsm-event! world play-sound! skin stage textures (:effect/target effect-ctx) world-mouse-position :kill)

      :effects.target/melee-damage
      ; TODO AT EFFECT CREATION MAKE
      ; same @ applicable
      (handle-effect! db schemas world play-sound! skin stage textures
                      [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                      effect-ctx
                      world-mouse-position
                      apply-effects!
                      active-entities
                      raycaster)

      :effects.target/spiderweb
      (let [target (:effect/target effect-ctx)]
        ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
        (when-not (:entity/temp-modifier @target)
          (swap! target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                     :counter (timer/create elapsed-time* spiderweb-duration)})
          (swap! target update :entity/stats stats/add-mods spiderweb-modifiers)
          nil))

      :effects.target/stun
      (handle-fsm-event! world play-sound! skin stage textures (:effect/target effect-ctx) world-mouse-position :stun v))))

(defn- usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (> (:skill/cost skill) ((stats/get-mana (:entity/stats entity)) 0))
   :not-enough-mana

   (not (seq (filter #(effect-applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))

(defn create-skin! [^FileHandle file-handle]
  (let [s (Skin. file-handle)]
    (set! (.markupEnabled ^BitmapFont$BitmapFontData
                          (.getData (.getFont ^Skin s "default-font")))
          true)
    s))

(defn create-cursors! [files]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                         cursor (.newCursor ^Graphics Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                     (Disposable/.dispose pixmap*)
                     cursor)))))

(defn create-world-viewport! [world-unit-scale]
  (let [world-width (* 1440 world-unit-scale)
        world-height (* 900 world-unit-scale)]
    (FitViewport. (float world-width)
                  (float world-height)
                  (doto (OrthographicCamera.)
                    (.setToOrtho false world-width world-height)))))

(defn create-default-font! [files {:keys [path
                                          size
                                          quality-scaling
                                          use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. ^FileHandle (.internal ^Files files path))
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

(defn create-level! [level-fn db* textures*]
  (let [{level-tiled-map :tiled-map
         level-start :start-position}
        (level-fn {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                               (db/all-raw db* :properties/creatures)
                                               #(textures/texture-region textures* %))
                   :textures textures*})]
    {:tiled-map level-tiled-map
     :start-position level-start}))

(defn spawn-player! [db* schemas world start-position skin stage textures*]
  (let [eid (spawn-entity! @world (spawn-creature {:position (mapv (partial + 0.5) @start-position)
                                            :creature-property (db/build db* schemas :creatures/vampire)
                                            :components {:entity/fsm {:fsm :fsms/player
                                                                      :initial-state :player-idle}
                                                         :entity/faction :good
                                                         :entity/player? true
                                                         :entity/free-skill-points 3
                                                         :entity/clickable {:type :clickable/player}
                                                         :entity/click-distance-tiles 1.5}}))]
    (ui/sync-player-ui! skin stage textures* @(:world/elapsed-time @world) eid)))

(defn bind-player-eid! [world player-eid]
  (let [eid (get @(:world/entity-ids @world) 1)]
    (assert (:entity/player? @eid))
    (reset! player-eid eid)))

(defn spawn-map-creatures! [db* schemas world start-position tiled-map]
  (let [sp @start-position]
    (doseq [[position creature-id] (moon-tiled-map/spawn-positions @tiled-map)
            :when (not= position sp)]
      (spawn-entity! @world (spawn-creature {:position (mapv (partial + 0.5) position)
                                      :creature-property (db/build db* schemas (keyword creature-id))
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-sleeping}
                                                   :entity/faction :evil}})))))


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
  [skin stage mouseover-eid world]
  (create-info-window
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(.getWorldWidth (.getViewport ^Stage stage)) 0]
    :set-label-text! (fn []
                       (if-let [eid @mouseover-eid]
                         (ui/info-text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    @(:world/elapsed-time @world))
                         ""))
    :skin skin}))

(defn hp-mana-bar-create
  [default-font stage textures unit-scale player-eid]
  (let [{:keys [rahmen-file
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
    (proxy [Actor] []
      (act [delta]
        (let [^Actor this this]
          (proxy-super act delta)))
      (draw [batch parent-alpha]
        (when (.getStage ^Actor this)
          (let [stats (:entity/stats @@player-eid)
                bar-x (- x (/ rahmenw 2))]
            (draw-hpmana-bar! nil batch bar-x y-hp hpcontent-file (stats/get-hitpoints stats) "HP")
            (draw-hpmana-bar! nil batch bar-x y-mana manacontent-file (stats/get-mana stats) "MP")))))))

(defn handle-clicked-inventory-cell
  [world player-eid play-sound! skin stage textures ui-set-item! ui-remove-item! cell world-mouse-position]
  (case (:state (:entity/fsm @player-eid))
    :player-idle
    (when-let [item (inventory/item-at (:entity/inventory @player-eid) cell)]
      (play-sound! "bfxr_takeit.wav")
      (swap! player-eid remove-item cell)
      (ui-remove-item! cell)
      (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :pickup-item item))

    :player-item-on-cursor
    (let [entity @player-eid
          inventory (:entity/inventory entity)
          item-in-cell (inventory/item-at inventory cell)
          item-on-cursor (:entity/item-on-cursor entity)]
      (cond
       (and (not item-in-cell)
            (inventory/valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play-sound! "bfxr_itemput.wav")
           (swap! player-eid set-item cell item-on-cursor)
           (ui-set-item! cell item-on-cursor)
           (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :dropped-item))

       (and item-in-cell
            (inventory/valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play-sound! "bfxr_itemput.wav")
           (swap! player-eid remove-item cell)
           (ui-remove-item! cell)
           (swap! player-eid set-item cell item-on-cursor)
           (ui-set-item! cell item-on-cursor)
           (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :dropped-item)
           (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :pickup-item item-in-cell))))

    nil))

(defn- inventory-window-cell [player-eid on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
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
  [{:keys [player-eid
           on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint ^Color (Color. 1 1 1 0.4))))
        ->cell (partial inventory-window-cell player-eid on-click-cell slot->drawable draw-cell-rect! cell-size)
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
  [on-click-cell draw-cell-rect! skin stage textures player-eid]
  (let [slot->y-sprite-idx #:inventory.slot {:weapon 0
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
     {:player-eid player-eid
      :on-click-cell on-click-cell
      :draw-cell-rect! draw-cell-rect!
      :skin skin
      :position [(.getWorldWidth (.getViewport ^Stage stage))
                 (.getWorldHeight (.getViewport ^Stage stage))]
      :slot->texture-region slot->texture-region
      :cell-size cell-size})))

(defn player-message-actor-create [default-font unit-scale]
  (let [message-duration-seconds 0.5]
    (doto (proxy [Actor] []
            (act [delta]
              (let [state (.getUserObject ^Actor this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil))))
              (let [^Actor this this]
                (proxy-super act delta)))
            (draw [batch parent-alpha]
              (when-let [stage (.getStage ^Actor this)]
                (let [state (.getUserObject ^Actor this)
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

(defn- entity-state-draw-ui-view
  [[k _v] eid batch unit-scale textures mouseover-actor* ui-mouse-pos]
  (case k
    :player-item-on-cursor
    (when mouseover-actor*
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
                              ui-mouse-pos
                              {:center? true}))

    nil))

(defn player-state-draw-create [unit-scale textures player-eid]
  (proxy [Actor] []
    (act [delta]
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^Actor this)
            player-eid @player-eid
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            mouse-pos (ui-mouse-position stage)
            [x y] mouse-pos]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   batch
                                   unit-scale
                                   textures
                                   (mouseover-actor stage x y)
                                   mouse-pos)))))

(defn windows-create [actor-fns]
  (let [group* (Group.)]
    (run! #(.addActor ^Group group* ^Actor %) (for [f actor-fns] (f)))
    (doto group*
      (.setName "moon.ui.windows"))))

(defn draw-cell-debug
  [ctx shape-drawer ^Viewport world-viewport world
   show-cell-entities? show-cell-occupied? show-potential-field-colors?
   factions-iterations]
  (let [world @world
        ^OrthographicCamera camera (.getCamera world-viewport)
        plane-points (mapv (fn [^Vector3 v3]
                             [(.x v3) (.y v3) (.z v3)])
                           (.planePoints (.frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))
        tile-positions (for [x (range (int left-x) (int right-x))
                             y (range (int bottom-y) (+ 2 (int top-y)))]
                         [x y])]
    (doseq [[[x y] cell*] (world/cells-at world tile-positions)]
      (when (and @show-cell-entities? (seq (:entities cell*)))
        (shape-drawer/filled-rectangle! shape-drawer x y 1 1 (color/float-bits 1 0 0 0.6)))
      (when (and @show-cell-occupied? (seq (:occupied cell*)))
        (shape-drawer/filled-rectangle! shape-drawer x y 1 1 (color/float-bits 0 0 1 0.6)))
      (when-let [faction @show-potential-field-colors?]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (shape-drawer/filled-rectangle! shape-drawer x y 1 1 (color/float-bits ratio (- 1 ratio) ratio 0.6)))))))))

(defn- draw-fn-grid [shape-drawer leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (shape-drawer/line! shape-drawer [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (shape-drawer/line! shape-drawer [leftx liney] [rightx liney] color-float-bits))))

(defn draw-tile-grid
  [ctx shape-drawer ^Viewport world-viewport show-tile-grid?]
  (when @show-tile-grid?
    (let [^OrthographicCamera camera (.getCamera world-viewport)
          plane-points (mapv (fn [^Vector3 v3]
                               [(.x v3) (.y v3) (.z v3)])
                             (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))]
      (draw-fn-grid shape-drawer
                    (int left-x)
                    (int bottom-y)
                    (inc (int (.getWorldWidth world-viewport)))
                    (+ 2 (int (.getWorldHeight world-viewport)))
                    1
                    1
                    (color/float-bits 1 1 1 0.8)))))

(defn highlight-mouseover-tile
  [ctx shape-drawer world-mouse-position world]
  (let [world @world
        [x y] (mapv int world-mouse-position)
        cell (world/cell-at world [x y])]
    (when (and cell (#{:air :none} (:movement cell)))
      (shape-drawer/rectangle! shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (color/float-bits 1 1 0 0.5)
                           :none (color/float-bits 1 0 0 0.5))))))

(def ^:private active-skill-radius
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- effect-render
  [[k v] effect-ctx shape-drawer active-entities raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (shape-drawer/line! shape-drawer
                      (:entity/position source*)
                      (:entity/position target*)
                      (color/float-bits 1 0 0 0.5))))

    :effects/target-entity
    (when-let [target (:effect/target effect-ctx)]
      (let [source (:effect/source effect-ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (shape-drawer/line! shape-drawer
                      (body/start-point body target-body)
                      (body/end-point body target-body maxrange)
                      (if (body/in-range? body target-body maxrange)
                        (color/float-bits 1 0 0 0.5)
                        (color/float-bits 1 1 0 0.5)))))

    nil))

(defn- draw-component
  [shape-drawer batch default-font unit-scale world-unit-scale mouseover-actor world-mouse-position
   textures player elapsed-time active-entities raycaster
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
        (shape-drawer/with-line-width shape-drawer 4
          (shape-drawer/line! shape-drawer position end color))
        (shape-drawer/line! shape-drawer position end color)))

    :entity/mouseover?
    (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
          color (cond (= faction (faction/enemy (:entity/faction player)))
                      (color/float-bits 1 0 0 0.4)
                      (= faction (:entity/faction player))
                      (color/float-bits 0 1 0 0.4)
                      :else
                      (color/float-bits 1 1 1 0.4))]
      (shape-drawer/with-line-width shape-drawer 5
        (shape-drawer/ellipse! shape-drawer position
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
          (shape-drawer/filled-rectangle! shape-drawer x y width height (color/float-bits 0 0 0 1))
          (shape-drawer/filled-rectangle! shape-drawer
                                    (+ x border)
                                    (+ y border)
                                    (- (* width ratio) (* 2 border))
                                    (- height (* 2 border))
                                    (cond
                                      (> ratio 0.75) (color/float-bits 0 0.8 0 1)
                                      (> ratio 0.5) (color/float-bits 0 0.5 0 1)
                                      (> ratio 0.25) (color/float-bits 0.5 0.5 0 1)
                                      :else (color/float-bits 0.5 0 0 1))))))

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
    (shape-drawer/filled-circle! shape-drawer (:entity/position entity) 0.5 (color/float-bits 0.5 0.5 0.5 0.4))

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
      (shape-drawer/filled-circle! shape-drawer center radius (color/float-bits 1 1 1 0.125))
      (shape-drawer/sector! shape-drawer
                      center
                      radius
                      (math/to-radians 90)
                      (math/to-radians (* (float action-counter-ratio) 360))
                      (color/float-bits 1 1 1 0.5))
      (draw-fn-texture-region batch unit-scale texture-region [(- (float x) radius) y])
      (doseq [effect effects]
        (effect-render effect effect-ctx shape-drawer
                       active-entities
                       raycaster)))

    :npc-sleeping
    (let [{:keys [entity/position entity/height]} entity
          [x y] position]
      (draw-fn-text batch default-font unit-scale {:text "zzz"
                         :x x
                         :y (+ y (/ height 2))
                         :up? true}))

    :stunned
    (shape-drawer/circle! shape-drawer (:entity/position entity) 0.5 (color/float-bits 1 1 1 0.6))))

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

(defn- draw-entity-rectangle!
  [ctx shape-drawer entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (shape-drawer/rectangle! shape-drawer x y width height color-float-bits)))

(defn draw-entities!
  [ctx shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor world-mouse-position
   player-eid raycaster world show-body-bounds? active-entities render-z-order]
  (let [player-eid @player-eid
        elapsed-time @(:world/elapsed-time @world)
        show-body-bounds? @show-body-bounds?
        active-entities @active-entities
        entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (sort-by (comp render-z-order key) <
                                        (group-by :entity/z-order entities))
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (when show-body-bounds?
        (draw-entity-rectangle! ctx shape-drawer
                                entity
                                (if (:entity/collides? entity)
                                  (color/float-bits 1 1 1 1)
                                  (color/float-bits 0.5 0.5 0.5 1))))
      (doseq [[k v] entity
              :when (get render-layer k)]
        (draw-component shape-drawer batch default-font unit-scale world-unit-scale mouseover-actor world-mouse-position
                        textures player elapsed-time active-entities raycaster
                        entity k v)))))

(defn- choose-skill [ray-blocked? entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect-applicable? e effect-ctx)))
                          (some (fn [e] (effect-useful? e effect-ctx ray-blocked?))))))
       first))

(defn- create-effect-ctx
  [world ctx eid]
  (let [world @world
        entity @eid
        target (potential-field/nearest-enemy (:world/grid world) entity)
        target (when (and target
                          (raycaster/line-of-sight? (:world/raycaster world) entity @target))
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
  [world delta-time play-sound! skin stage textures
   max-speed
   ctx world-mouse-position apply-effects! eid [k v]]
  (let [elapsed-time* @(:world/elapsed-time @world)]
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
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid assoc :entity/destroyed? true)
          (doseq [friendly-eid (let [circle {:position (:entity/position @eid)
                                             :radius 4}
                                     [x y] (:position circle)
                                     g2d (:world/grid @world)
                                     gdx-circle (Circle. (float x) (float y) (float (:radius circle)))]
                                 (->> circle
                                      circle/outer-rectangle
                                      rectangle/touched-tiles
                                      (keep #(get-in g2d %))
                                      (map deref)
                                      (into #{} (mapcat :entities))
                                      (filter #(Intersector/overlaps ^Circle gdx-circle
                                                                     ^Rectangle (body/rectangle @%)))
                                      (filter #(= (:entity/faction @%) faction))))]
            (handle-fsm-event! world play-sound! skin stage textures friendly-eid world-mouse-position :alert)))
        nil)

      :entity/string-effect
      (let [{:keys [counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid dissoc :entity/string-effect))
        nil)

      :entity/skills
      (do (doseq [{:keys [skill/cooling-down?] :as skill} (vals v)
                  :when (and cooling-down?
                             (timer/stopped? elapsed-time* cooling-down?))]
            (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
          nil)

      :entity/temp-modifier
      (let [{:keys [modifiers counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid dissoc :entity/temp-modifier)
          (swap! eid update :entity/stats stats/remove-mods modifiers))
        nil)

      :entity/projectile-collision
      (let [{:keys [entity-effects already-hit-bodies piercing?]} v
            world* @world
            entity @eid
            touched-cells (map deref (keep #(get-in (:world/grid world*) %) (body/touched-tiles entity)))
            hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                            (not= (:entity/faction entity)
                                                  (:entity/faction @%))
                                            (:entity/collides? @%)
                                            (body/overlaps? entity
                                                            @%))
                                      (into #{} (mapcat :entities) touched-cells)))
            destroy? (or (and hit-entity (not piercing?))
                         (some #(cell/blocked? % (:entity/z-order entity)) touched-cells))]
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
            effect-ctx (update-effect-ctx (:world/raycaster @world) effect-ctx)]
        (cond
         (not (seq (filter #(effect-applicable? % effect-ctx)
                           (:skill/effects skill))))
         (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :action-done)

         (timer/stopped? elapsed-time* counter)
         (do (apply-effects! effect-ctx (:skill/effects skill))
             (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :action-done)
             nil)))

      :entity/delete-after-duration
      (do (when (timer/stopped? elapsed-time* v)
            (swap! eid assoc :entity/destroyed? true))
          nil)

      :stunned
      (let [{:keys [counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :effect-wears-off)))

      :npc-moving
      (let [{:keys [timer]} v]
        (when (timer/stopped? elapsed-time* timer)
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :timer-finished)))

      :npc-sleeping
      (let [entity @eid]
        (when-let [distance (potential-field/nearest-enemy-distance (:world/grid @world) entity)]
          (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
            (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :alert))))

      :npc-idle
      (let [raycaster (:world/raycaster @world)
            effect-ctx (create-effect-ctx world ctx eid)]
        (if-let [skill (choose-skill (partial raycaster/blocked? raycaster) @eid effect-ctx)]
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :start-action [skill effect-ctx])
          (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-position :movement-direction (or (find-direction (:world/grid @world) eid)
                                                             [0 0]))))

      :entity/movement
      (let [{:keys [direction
                    speed]
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
          (let [world* @world
                movement (assoc movement :delta-time @delta-time)
                body @eid]
            (when-let [body (if (:entity/collides? body)
                               (grid/try-move-solid-body (:world/grid world*) body (:entity/id @eid) movement)
                               (let [{:keys [direction speed delta-time]} movement]
                                 (update body :entity/position
                                         #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))))]
              (swap! eid assoc :entity/position (:entity/position body))
              (relocate-eid! world* eid)
              nil))))

      nil)))

(def ^:private controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "Leftmouse click - use skill/drop item on cursor"]))

(def ^:private help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(defn- debug-flags-menu-item
  [show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?]
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

(def ^:private select-world-menu-item
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

(defn- dev-menus
  [show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?]
  [(debug-flags-menu-item show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
   help-menu-item
   select-world-menu-item])

(defn create-ui-actors
  [world mouseover-eid paused? player-eid
   show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?
   unit-scale
   play-sound! default-font shape-drawer skin stage textures* world-viewport]
  (let [cell-size 48]
    [(create-action-bar)
     (dev-menu/create
      {:menus (dev-menus show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
       :update-labels (for [item [{:label "elapsed-time"
                                   :update-fn (fn []
                                                (str (number/readable @(:world/elapsed-time @world)) " seconds"))
                                   :icon "images/clock.png"}
                                  {:label "FPS"
                                   :update-fn (fn [] (.getFramesPerSecond ^Graphics Gdx/graphics))
                                   :icon "images/fps.png"}
                                  {:label "Mouseover-entity id"
                                   :update-fn (fn []
                                                (when-let [entity (and @mouseover-eid @@mouseover-eid)]
                                                  (:entity/id entity)))
                                   :icon "images/mouseover.png"}
                                  {:label "paused?"
                                   :update-fn (fn [] @paused?)}
                                  {:label "GUI"
                                   :update-fn (fn []
                                                (mapv int (ui-mouse-position stage)))}
                                  {:label "World"
                                   :update-fn (fn []
                                                (mapv int (world-mouse-position world-viewport)))}
                                  {:label "Zoom"
                                   :update-fn (fn []
                                                (.zoom ^OrthographicCamera (.getCamera ^Viewport world-viewport)))
                                   :icon "images/zoom.png"}]]
                        (if (:icon item)
                          (update item :icon #(get textures* %))
                          item))
       :skin skin})
     (hp-mana-bar-create default-font stage textures* unit-scale player-eid)
     (windows-create [#(stage-info-window-create skin stage mouseover-eid world)
                      #(inventory-window-create
                        (fn [_event cell]
                          (handle-clicked-inventory-cell world
                                                         @player-eid
                                                         play-sound!
                                                         skin
                                                         stage
                                                         textures*
                                                         (fn [cell item] (ui/ui-set-item! skin stage textures* cell item))
                                                         (fn [cell] (ui/ui-remove-item! stage cell))
                                                         cell
                                                         (world-mouse-position world-viewport)))
                        (fn [player-entity x y mouseover? cell]
                          (shape-drawer/rectangle! shape-drawer x y cell-size cell-size (color/float-bits 0.5 0.5 0.5 1))
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          (color/float-bits 0 0.6 0 0.8)
                                          (color/float-bits 0.6 0 0 0.8))]
                              (shape-drawer/filled-rectangle! shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color))))
                        skin
                        stage
                        textures*
                        player-eid)])
     (player-state-draw-create unit-scale textures* player-eid)
     (player-message-actor-create default-font unit-scale)]))

(defn update-mouseover-eid! [world player-eid mouseover-eid stage world-viewport render-z-order]
  (let [old-mouseover-eid @mouseover-eid
        [x y] (ui-mouse-position stage)
        new-eid (if (mouseover-actor stage x y)
                  nil
                  (let [player @@player-eid
                        world* @world
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (grid/point->entities (:world/grid world*) (world-mouse-position world-viewport)))]
                    (->> hits
                         (sort-by #((:entity/z-order @%) render-z-order) <)
                         reverse
                         (filter #(raycaster/line-of-sight? (:world/raycaster world*) player @%))
                         first)))]
    (when old-mouseover-eid
      (swap! old-mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (reset! mouseover-eid new-eid)))

(defn update-active-entities! [world player-eid active-entities]
  (reset! active-entities (content-grid/active-entities (:world/content-grid @world) @@player-eid)))

(defn set-camera-to-player! [player-eid world-viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)
        [x y] (:entity/position @@player-eid)]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn draw-tiled-map! [batch world-viewport tiled-map world world-unit-scale]
  (let [world* @world
        tiled-map @tiled-map
        ^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)
        ^Batch batch batch]
    (.setProjectionMatrix batch (.combined camera))
    (.begin batch)
    (tiled-map.draw/draw! tiled-map
                          batch
                          world-unit-scale
                          camera
                          (tile-color-setter*
                           {:ray-blocked? (partial raycaster/blocked? (:world/raycaster world*))
                            :explored-tile-corners (:world/explored-tile-corners world*)
                            :light-position [(.x pos) (.y pos) (.z pos)]
                            :see-all-tiles? false
                            :explored-tile-color (color/float-bits 0.5 0.5 0.5 1)
                            :visible-tile-color (color/float-bits 1 1 1 1)
                            :invisible-tile-color (color/float-bits 0 0 0 1)}))
    (.end batch)))

(defn current-mouseover-actor [stage]
  (let [[x y] (ui-mouse-position stage)]
    (mouseover-actor stage x y)))

(defn update-cursor! [cursors interaction-state player-eid]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (get {:player-item-on-cursor :cursors/hand-grab
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
                                                                                  (inventory/item-at (:entity/inventory @eid) inventory-slot)))]
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
                                           :cursors/no-skill-selected)))}
                       state-k)
        cursor-key (if (keyword? cursor-fn)
                     cursor-fn
                     (cursor-fn eid))]
    (assert (contains? cursors cursor-key))
    (.setCursor ^Graphics Gdx/graphics ^Cursor (get cursors cursor-key))))

(defn destroy-entities! [db schemas world play-sound! skin stage textures]
  (doseq [eid (filter (comp :entity/destroyed? deref) (vals @(:world/entity-ids @world)))]
    (unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (audiovisual! db schemas world play-sound! (:entity/position @eid) v)
        nil))))

(defn draw-world!
  [batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
   world player-eid show-body-bounds? active-entities
   show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
   factions-iterations render-z-order]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position world-viewport)]
    (.setColor ^Batch batch (float 1) (float 1) (float 1) (float 1))
    (.setProjectionMatrix ^Batch batch (.combined camera))
    (.begin ^Batch batch)
    (shape-drawer/with-line-width shape-drawer world-unit-scale
      (reset! unit-scale world-unit-scale)
      (doseq [draw-fn [#(draw-tile-grid % shape-drawer world-viewport show-tile-grid?)
                       #(draw-cell-debug % shape-drawer world-viewport world
                                         show-cell-entities? show-cell-occupied? show-potential-field-colors?
                                         factions-iterations)
                       #(draw-entities! % shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor* world-mouse-pos
                                        player-eid (:world/raycaster @world) world show-body-bounds? active-entities render-z-order)
                       #(highlight-mouseover-tile % shape-drawer world-mouse-pos world)]]
        (draw-fn nil))
      (reset! unit-scale 1))
    (.end ^Batch batch)))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce #(mapv + %1 %2) [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn- show-message! [stage message]
  (-> (.getRoot ^Stage stage)
      (.findActor "player-message")
      (.setUserObject (atom {:text message :counter 0}))))

(defn- interaction-state->txs [[k params] world stage play-sound! skin textures ui-set-item! player-eid world-mouse-position]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          (do (ui/toggle-inventory-visible! stage)
              nil)

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (-> (.getRoot ^Stage stage)
                  (.findActor "moon.ui.windows.inventory")
                  .isVisible)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! "bfxr_takeit.wav")
                  (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :pickup-item item))

              (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! "bfxr_pickup.wav")
                  (assert (item/valid? item))
                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @player-eid) item)]
                    (assert cell)
                    (assert (nil? cell-item))
                    (swap! player-eid set-item cell item)
                    (ui-set-item! cell item))
                  nil)

              :else
              (do (play-sound! "bfxr_denied.wav")
                  (show-message! stage "Your Inventory is full")
                  nil))))
        (do (play-sound! "bfxr_denied.wav")
            (show-message! stage "Too far away")
            nil)))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (handle-fsm-event! world play-sound! skin stage textures player-eid world-mouse-position :start-action [skill effect-ctx]))

    :interaction-state.skill/not-usable
    (let [state params]
      (do (play-sound! "bfxr_denied.wav")
          (show-message! stage (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
          nil))

    :interaction-state/no-skill-selected
    (do (play-sound! "bfxr_denied.wav")
        (show-message! stage "No selected skill")
        nil)))

(defn handle-player-input!
  [world interaction-state player-eid
   play-sound! skin stage textures world-viewport key-pressed? button-just-pressed?]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        left-button-pressed? (button-just-pressed? Input$Buttons/LEFT)
        move-v (movement-vector key-pressed?)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position world-viewport)]
    (case state-k
      :player-idle
      (if move-v
        (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-pos :movement-input move-v)
        (when left-button-pressed?
          (interaction-state->txs @interaction-state
                                  world
                                  stage
                                  play-sound!
                                  skin
                                  textures
                                  #(ui/ui-set-item! skin stage textures %1 %2)
                                  eid
                                  world-mouse-pos)))

      :player-moving
      (if move-v
        (do (swap! eid assoc :entity/movement {:direction move-v
                                               :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                          0)})
            nil)
        (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-pos :no-movement-input))

      :player-item-on-cursor
      (when (and left-button-pressed?
                 (not mouseover-actor*))
        (handle-fsm-event! world play-sound! skin stage textures eid world-mouse-pos :drop-item))

      nil)))

(defn- tick-entities!
  [db schemas world delta-time active-entities
   play-sound! skin stage textures max-speed world-viewport]
  (let [active-entities* @active-entities
        raycaster (:world/raycaster @world)
        world-mouse-pos (world-mouse-position world-viewport)]
    (letfn [(apply-effects! [effect-ctx effects]
              (doseq [e (filter #(effect-applicable? % effect-ctx) effects)]
                (handle-effect! db schemas world play-sound! skin stage textures e effect-ctx world-mouse-pos
                               apply-effects!
                               active-entities*
                               raycaster)))]
      (doseq [eid active-entities*
              component @eid]
        (tick-component world delta-time play-sound! skin stage textures
                        max-speed
                        nil world-mouse-pos
                        apply-effects!
                        eid component)))))

(defn tick-game!
  [db schemas world delta-time potential-field-cache active-entities paused?
   play-sound! skin stage textures world-viewport factions-iterations
   max-delta max-speed]
  (when-not @paused?
    (let [delta-ms (min (.getDeltaTime ^Graphics Gdx/graphics) max-delta)]
      (reset! delta-time delta-ms)
      (swap! (:world/elapsed-time @world) + delta-ms))
    (doseq [[faction max-iterations] factions-iterations]
      (potential-field.update/update! (:world/grid @world)
                                      potential-field-cache
                                      faction
                                      @active-entities
                                      max-iterations))
    (tick-entities! db schemas world delta-time active-entities
                    play-sound! skin stage textures max-speed world-viewport)))

(def listener
  (let [world-unit-scale (float (/ 48))
        minimum-size 0.39
        max-delta 0.04
        max-speed (/ minimum-size max-delta)
        z-orders [:z-order/on-ground
                  :z-order/ground
                  :z-order/flying
                  :z-order/effect]
        render-z-order (apply hash-map (interleave z-orders (range)))
        factions-iterations {:good 15
                             :evil 5}
        level-fn uf-caves/create
        default-font-params {:path "fonts/films.EXL_____.ttf"
                             :size 16
                             :quality-scaling 2
                             :use-integer-positions? false}
        sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)
        audio (atom nil)
        play-sound! (fn [sound-name]
                      (let [sounds @audio]
                        (assert (contains? sounds sound-name) (str sound-name))
                        (.play ^Sound (get sounds sound-name))))
        batch (atom nil)
        cursors (atom nil)
        default-font (atom nil)
        world-viewport (atom nil)
        shape-drawer (atom nil)
        shape-drawer-texture (atom nil)
        textures (atom nil)
        skin (atom nil)
        stage (atom nil)
        schemas (-> "schema.edn" io/resource slurp edn/read-string)
        db (db/create schemas)
        world (atom nil)
        tiled-map (atom nil)
        start-position (atom nil)
        player-eid (atom nil)
        potential-field-cache (atom nil)
        active-entities (atom nil)
        delta-time (atom nil)
        mouseover-eid (atom nil)
        paused? (atom false)
        show-potential-field-colors? (atom nil)
        show-cell-entities? (atom false)
        show-cell-occupied? (atom false)
        show-body-bounds? (atom false)
        show-tile-grid? (atom false)
        interaction-state (atom nil)
        unit-scale (atom 1)
        pausing? true
        state->pause-game? {:active-skill false
                            :stunned false
                            :player-moving false
                            :player-idle true
                            :player-dead true
                            :player-item-on-cursor true}]
    (reify ApplicationListener
      (create [_]
        (reset! audio (into {}
                            (for [path sound-paths]
                              [path (.newSound Gdx/audio (.internal Gdx/files path))])))
        (reset! batch (SpriteBatch.))
        (reset! unit-scale 1)
        (reset! shape-drawer-texture (white-pixel-texture))
        (reset! shape-drawer (shape-drawer/create @batch
                                                  (TextureRegion. ^Texture @shape-drawer-texture
                                                                 (int 1) (int 0) (int 1) (int 1))))
        (reset! skin (create-skin! (.internal Gdx/files "skin/uiskin.json")))
        (let [s (Stage. (FitViewport. (float 1440) (float 900)) @batch)]
          (.setInputProcessor ^Input Gdx/input ^InputProcessor s)
          (reset! stage s))
        (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0)
        (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1))
        (reset! cursors (create-cursors! Gdx/files))
        (reset! textures (textures/create Gdx/files {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}))
        (reset! world-viewport (create-world-viewport! world-unit-scale))
        (reset! default-font (create-default-font! Gdx/files default-font-params))
        (let [{level-tiled-map :tiled-map
               level-start :start-position} (create-level! level-fn db @textures)]
          (reset! tiled-map level-tiled-map)
          (reset! start-position level-start))
        (reset! world (world/create @tiled-map {:z-orders z-orders
                                                :minimum-size minimum-size}))
        (doseq [^Actor actor (create-ui-actors world
                                               mouseover-eid
                                               paused?
                                               player-eid
                                               show-tile-grid?
                                               show-cell-entities?
                                               show-cell-occupied?
                                               show-body-bounds?
                                               show-potential-field-colors?
                                               unit-scale
                                               play-sound!
                                               @default-font
                                               @shape-drawer
                                               @skin
                                               @stage
                                               @textures
                                               @world-viewport)]
          (.addActor ^Stage @stage actor))
        (spawn-player! db schemas world start-position @skin @stage @textures)
        (bind-player-eid! world player-eid)
        (spawn-map-creatures! db schemas world start-position tiled-map))

      (dispose [_]
        (run! Sound/.dispose (vals @audio))
        (Disposable/.dispose @batch)
        (run! Disposable/.dispose (vals @cursors))
        (Disposable/.dispose @default-font)
        (Disposable/.dispose @shape-drawer-texture)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures))
        (Disposable/.dispose @tiled-map))

      (render [_]
        (let [input Gdx/input
              key-pressed? #(.isKeyPressed ^Input input (int %))
              key-just-pressed? #(.isKeyJustPressed ^Input input (int %))
              button-just-pressed? #(.isButtonJustPressed ^Input input (int %))
              batch @batch
              cursors @cursors
              default-font @default-font
              shape-drawer @shape-drawer
              skin @skin
              stage @stage
              textures @textures
              world-viewport @world-viewport]
          (ScreenUtils/clear 0 0 0 0)
          (update-mouseover-eid! world player-eid mouseover-eid stage world-viewport render-z-order)
          (update-active-entities! world player-eid active-entities)
          (set-camera-to-player! player-eid world-viewport)
          (draw-tiled-map! batch world-viewport tiled-map world world-unit-scale)
          (draw-world! batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
                       world player-eid show-body-bounds? active-entities
                       show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
                       factions-iterations render-z-order)
          (reset! interaction-state
                  (let [player-eid @player-eid
                        mouseover-eid @mouseover-eid
                        mouseover-actor (current-mouseover-actor stage)
                        world-mouse-position (world-mouse-position world-viewport)]
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
                                            action-bar/selected-skill)]
                        (let [entity @player-eid
                              skill (skill-id (:entity/skills entity))
                              target-position (or (and mouseover-eid
                                                       (:entity/position @mouseover-eid))
                                                  world-mouse-position)
                              effect-ctx {:effect/source player-eid
                                          :effect/target mouseover-eid
                                          :effect/target-position target-position
                                          :effect/target-direction (v2/direction (:entity/position @player-eid)
                                                                                target-position)}
                              state (usable-state skill entity effect-ctx)]
                          (if (= state :usable)
                            [:interaction-state.skill/usable [skill effect-ctx]]
                            [:interaction-state.skill/not-usable state]))
                        [:interaction-state/no-skill-selected]))))
          (update-cursor! cursors interaction-state player-eid)
          (handle-player-input! world interaction-state player-eid
                                play-sound! skin stage textures world-viewport
                                key-pressed? button-just-pressed?)
          (reset! interaction-state nil)
          (reset! paused?
                  (or #_error
                      (and pausing?
                           (state->pause-game? (:state (:entity/fsm @@player-eid)))
                           (not (or (key-just-pressed? (:unpause-once controls))
                                    (key-pressed? (:unpause-continously controls)))))))
          (tick-game! db schemas world delta-time potential-field-cache active-entities paused?
                      play-sound! skin stage textures world-viewport factions-iterations
                      max-delta max-speed)
          (destroy-entities! db schemas world play-sound! skin stage textures)
          (doseq [[k f] {(:zoom-in controls) (fn []
                                               (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)]
                                                 (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.025)))
                                                 (.update camera)))
                         (:zoom-out controls) (fn []
                                                (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)]
                                                  (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.025)))
                                                  (.update camera)))}]
            (when (key-pressed? k)
              (f)))
          (doseq [[k f] {(:close-windows-key controls) (fn []
                                                         (->> (.getChildren ^Group (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows"))
                                                              (run! #(.setVisible ^Actor % false))))
                         (:toggle-inventory controls) #(ui/toggle-inventory-visible! stage)
                         (:toggle-entity-info controls) (fn []
                                                          (let [entity-info (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
                                                            (.setVisible ^Actor entity-info
                                                                         (not (.isVisible ^Actor entity-info)))))}]
            (when (key-just-pressed? k)
              (f)))
          (let [^Stage stage stage]
            (.act stage)
            (.draw stage))))

      (resize [_ width height]
        (.update (.getViewport ^Stage @stage) width height true)
        (.update ^Viewport @world-viewport width height false))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
