(ns game.shared
  (:require [clojure.string :as str]
            [entity.spawn :refer [create-entity-state spawn-entity!]]
            [game.entity :refer [add-text-effect]]
            [game.target-all :refer [affected-targets]]
            [moon.body :as body]
            [moon.db :as db]
            [moon.faction :as faction]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.m :as m]
            [moon.number :as number]
            [moon.rand :as rand]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.v2 :as v2]
            [moon.val-max :as val-max]
            [moon.world :as world]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx Gdx Input Input$Keys)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData TextureRegion)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup Image Label Skin TextButton TextTooltip Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Drawable TextureRegionDrawable)
           (com.badlogic.gdx.utils Align)
           (com.badlogic.gdx.utils.viewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn- action-bar-get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject ^Actor group)}))

(defn- action-bar-remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (get horizontal-group skill-id)]
    (.remove ^Actor button)
    (.remove ^ButtonGroup button-group ^Button button)
    nil))

(defn action-bar-selected-skill [action-bar]
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

(defn item-place-position [player-position world-mouse-position maxrange]
  (v2/add player-position
          (v2/scale (v2/direction player-position world-mouse-position)
                    (min maxrange
                         (v2/distance player-position world-mouse-position)))))

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

(defn ui-set-item! [skin stage textures cell item]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-set-item! cell
                                  {:texture-region (textures/texture-region textures (:entity/image item))
                                   :tooltip-text (item/info-text item)}
                                  skin)))

(defn play-sound! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (.play ^Sound (get sounds sound-name)))

(defn audiovisual! [db world elapsed-time audio skin stage textures z-orders minimum-size position audiovisual]
  (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (db/build db audiovisual)
                                             audiovisual)]
    (play-sound! audio sound)
    (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                   (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

(defn handle-fsm-event! [world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position event & [params]]
  (let [elapsed-time* @elapsed-time
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
                (play-sound! audio "bfxr_itemputground")
                (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                               (spawn-item (item-place-position (:entity/position entity)
                                                                world-mouse-position
                                                                (- (:entity/click-distance-tiles entity) 0.1))
                                           item))))

            :player-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            :npc-sleeping
            (do (swap! eid add-text-effect elapsed-time* "[WHITE]!" 1)
                (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
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
            (let [{:keys [skill]} state-v]
              (swap! eid update :entity/stats stats/pay-mana-cost (:skill/cost skill))
              (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
                     (timer/create elapsed-time* (:skill/cooldown skill)))
              (play-sound! audio (:skill/start-action-sound skill))
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
            (do (play-sound! audio "bfxr_playerdeath")
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

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

; handle-fsm-event haengt an handle-effect
(defn handle-effect
  [db world elapsed-time audio skin stage textures z-orders minimum-size
   [k v] effect-ctx world-mouse-position apply-effects!
   active-entities colors raycaster]
  (let [elapsed-time* @elapsed-time]
    (case k
      :effects/audiovisual
      (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:effect/target-position effect-ctx) v)

      :effects/projectile
      (let [source (:effect/source effect-ctx)
            source* @source
            direction (:effect/target-direction effect-ctx)
            size (:projectile/size v)]
        (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                       (spawn-projectile
                        {:position (v2/add (:entity/position source*)
                                           (v2/scale direction
                                                     (+ (/ (:entity/width source*) 2) size 0.1)))
                         :direction direction
                         :faction (:entity/faction source*)}
                        v)))

      :effects/spawn
      (let [source (:effect/source effect-ctx)]
        (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                       (spawn-creature {:position (:effect/target-position effect-ctx)
                                        :creature-property v
                                        :components {:entity/fsm {:fsm :fsms/npc
                                                                  :initial-state :npc-idle}
                                                     :entity/faction (:entity/faction @source)}})))

      :effects/target-all
      (let [source (:effect/source effect-ctx)
            source* @source]
        (doseq [target (affected-targets active-entities raycaster source*)]
          (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                         (spawn-line
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
          (do (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                             (spawn-line
                              {:start (body/start-point body target-body)
                               :end (:entity/position target-body)
                               :duration 0.05
                               :color (:colors/target-entity-line colors)
                               :thick? true}))
              (apply-effects! effect-ctx entity-effects))
          (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size
                        (body/end-point body target-body maxrange)
                        :audiovisuals/hit-ground)))

      :effects.target/audiovisual
      (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:entity/position @(:effect/target effect-ctx)) v)

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
               dmg-amount (rand/int-between min-max)
               new-hp-val (max (- (hp 0) dmg-amount)
                               0)
               dmg-text (str "[RED]" dmg-amount "[]")]
           (swap! target assoc-in [:entity/stats :stats/hp 0] new-hp-val)
           (swap! target add-text-effect elapsed-time* dmg-text 0.3)
           (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size target world-mouse-position (if (zero? new-hp-val) :kill :alert))
           (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:entity/position target*) :audiovisuals/damage))))

      :effects.target/kill
      (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size (:effect/target effect-ctx) world-mouse-position :kill)

      :effects.target/melee-damage
      ; TODO AT EFFECT CREATION MAKE
      ; same @ applicable
      (handle-effect db world elapsed-time audio skin stage textures z-orders minimum-size
                     [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                     effect-ctx
                     world-mouse-position
                     apply-effects!
                     active-entities
                     colors
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
      (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size (:effect/target effect-ctx) world-mouse-position :stun v))))

(defn toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (.findActor "moon.ui.windows.inventory"))]
    (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory)))))

(defn- float-bits [[r g b a]]
  (Color/toFloatBits (float r) (float g) (float b) (float a)))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (float-bits [1 1 0 0.5])
     :colors/mouseover-tile-none (float-bits [1 0 0 0.5])
     :colors/debug-body-outline-collides (float-bits [1 1 1 1])
     :colors/debug-body-outline (float-bits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (float-bits [1 0 0 1])
     :colors/debug-cell-entities (float-bits [1 0 0 0.6])
     :colors/debug-cell-occupied (float-bits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (float-bits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (float-bits [1 0 0 0.75])
     :colors/target-all-render (float-bits [1 0 0 0.5])
     :colors/target-entity-line (float-bits [1 0 0 0.75])
     :colors/target-entity-in-range (float-bits [1 0 0 0.5])
     :colors/target-entity-not-in-range (float-bits [1 1 0 0.5])
     :colors/enemy-color (float-bits [1 0 0 outline-alpha])
     :colors/friendly-color (float-bits [0 1 0 outline-alpha])
     :colors/neutral-color (float-bits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (cond
                        (> ratio 0.75) (float-bits [0 0.8 0 1])
                        (> ratio 0.5) (float-bits [0 0.5 0 1])
                        (> ratio 0.25) (float-bits [0.5 0.5 0 1])
                        :else (float-bits [0.5 0 0 1])))
     :colors/hp-bar-rect (float-bits [0 0 0 1])
     :colors/temp-modifier (float-bits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (float-bits [1 1 1 0.125])
     :colors/active-skill-sector (float-bits [1 1 1 0.5])
     :colors/stunned (float-bits [1 1 1 0.6])
     :colors/explored-tile (float-bits [0.5 0.5 0.5 1])
     :colors/visible-tile (float-bits [1 1 1 1])
     :colors/invisible-tile (float-bits [0 0 0 1])
     :colors/droppable-item (float-bits [0 0.6 0 0.8])
     :colors/not-allowed-drop-item (float-bits [0.6 0 0 0.8])
     :colors/item-rect (float-bits [0.5 0.5 0.5 1])}))

(def controls
  {:zoom-in Input$Keys/MINUS
   :zoom-out Input$Keys/EQUALS
   :unpause-once Input$Keys/P
   :unpause-continously Input$Keys/SPACE
   :close-windows-key Input$Keys/ESCAPE
   :toggle-inventory Input$Keys/I
   :toggle-entity-info Input$Keys/E})

(defn draw-fn-filled-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledRectangle ^ShapeDrawer shape-drawer (float x) (float y) (float w) (float h)))

(defn draw-fn-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.rectangle ^ShapeDrawer shape-drawer x y w h))

(defn draw-fn-text [batch default-font unit-scale {:keys [font scale x y text up?]}]
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

(defn draw-fn-texture-region [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
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

(defn ui-remove-item! [stage cell]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-remove-item! cell)))
