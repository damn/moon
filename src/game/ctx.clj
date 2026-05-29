(ns game.ctx
  (:require [clojure.animation :as animation]
            [clojure.core-ext :refer [actions!
                                      reduce-actions!
                                      safe-merge
                                      ]]
            [clojure.math :as math]
            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [gdx.application :as app]
            [gdx.input :as input]
            [gdx.input.buttons :as input.buttons]
            [gdx.input.keys :as input.keys]
            [game.effect :as effect]
            ;; TODO accessing 'game.ctx'
            [game.entity :as entity]
            [game.state :as state]
            ;;
            [game.info :as info]
            [gdx.audio.sound :as sound]
            [gdx.graphics.shape-drawer :as shape-drawer]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.faction :as faction]
            [moon.grid :as grid]
            [moon.grid2d :as g2d]
            [moon.inventory :as inventory]
            [moon.raycaster :as raycaster]
            [game.skill :as skill]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.ui.inventory-window :as inventory-window]
            [moon.number :as number]
            [moon.val-max :as val-max]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.utils Align)))

(q/defrecord Entity [entity/body])

(def txs-fn-map
  {
   :tx/state-exit               (fn [ctx eid [state-k state-v]]
                                  (state/exit [state-k state-v] eid ctx))
   :tx/audiovisual              (fn
                                  [{:keys [ctx/db]} position audiovisual]
                                  (let [{:keys [tx/sound
                                                entity/animation]} (if (keyword? audiovisual)
                                                                     (db/build db audiovisual)
                                                                     audiovisual)]
                                    [[:tx/sound sound]
                                     [:tx/spawn-effect
                                      position
                                      {:entity/animation (assoc animation :delete-after-stopped? true)}]]))
   :tx/assoc                    (fn [_ctx eid k value]
                                  (swap! eid assoc k value)
                                  nil)
   :tx/assoc-in                 (fn [_ctx eid ks value]
                                  (swap! eid assoc-in ks value)
                                  nil)
   :tx/dissoc                   (fn [_ctx eid k]
                                  (swap! eid dissoc k)
                                  nil)
   :tx/update                   (fn [_ctx eid & params]
                                  (apply swap! eid update params)
                                  nil)
   :tx/mark-destroyed           (fn [_ctx eid]
                                  (swap! eid assoc :entity/destroyed? true)
                                  nil)
   :tx/set-cooldown             (fn [{:keys [ctx/elapsed-time]} eid skill]
                                  (swap! eid assoc-in [:entity/skills
                                                       (:property/id skill)
                                                       :skill/cooling-down?]
                                         (timer/create elapsed-time (:skill/cooldown skill)))
                                  nil)
   :tx/add-text-effect          (fn [{:keys [ctx/elapsed-time]} eid text duration]
                                  [[:tx/assoc
                                    eid
                                    :entity/string-effect
                                    (if-let [existing (:entity/string-effect @eid)]
                                      (-> existing
                                          (update :text str "\n" text)
                                          (update :counter timer/increment duration))
                                      {:text text
                                       :counter (timer/create elapsed-time duration)})]])
   :tx/add-skill                (fn [_ctx eid {:keys [property/id] :as skill}]
                                  {:pre [(not (contains? (:entity/skills @eid) id))]}
                                  (swap! eid update :entity/skills assoc id skill)
                                  nil)
   :tx/set-item                 (fn [_ctx eid cell item]
                                  (let [entity @eid
                                        inventory (:entity/inventory entity)]
                                    (assert (and (nil? (get-in inventory cell))
                                                 (inventory/valid-slot? cell item)))
                                    (swap! eid assoc-in (cons :entity/inventory cell) item)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/add (:stats/modifiers item)))
                                    nil))
   :tx/remove-item              (fn [_ctx eid cell]
                                  (let [entity @eid
                                        item (get-in (:entity/inventory entity) cell)]
                                    (assert item)
                                    (swap! eid assoc-in (cons :entity/inventory cell) nil)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
                                    nil))
   :tx/pickup-item              (fn [_ctx eid item]
                                  (inventory/assert-valid-item? item)
                                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
                                    (assert cell)
                                    (assert (or (inventory/stackable? item cell-item)
                                                (nil? cell-item)))
                                    (if (inventory/stackable? item cell-item)
                                      (do
                                       #_(tx/stack-item ctx eid cell item))
                                      [[:tx/set-item eid cell item]])))
   :tx/event                    (fn send-event!
                                  ([ctx eid event]
                                   (send-event! ctx eid event nil))
                                  ([ctx eid event params]
                                   (let [fsm (:entity/fsm @eid)
                                         _ (assert fsm)
                                         old-state-k (:state fsm)
                                         new-fsm (fsm/fsm-event fsm event)
                                         new-state-k (:state new-fsm)]
                                     (when-not (= old-state-k new-state-k)
                                       (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                                                             [k (k @eid)])
                                             new-state-obj [new-state-k (state/create [new-state-k params] eid ctx)]]
                                         [[:tx/assoc       eid :entity/fsm new-fsm]
                                          [:tx/assoc       eid new-state-k (new-state-obj 1)]
                                          [:tx/dissoc      eid old-state-k]
                                          [:tx/state-exit  eid old-state-obj]
                                          [:tx/state-enter eid new-state-obj]])))))
   :tx/register-eid             (fn [ctx eid]
                                  (assert (and (not (contains? @eid :entity/id))))
                                  (let [id (swap! (:ctx/id-counter ctx) inc)]
                                    (assert (number? id))
                                    (swap! eid assoc :entity/id id)
                                    (swap! (:ctx/entity-ids ctx) assoc id eid))

                                  (assert (:entity/body @eid)) ; -< inside content grid
                                  (content-grid/add-entity! (:ctx/content-grid ctx) eid)

                                  (assert (:entity/body @eid)) ; <- inside the grid add fn ?
                                  (when (:body/collides? (:entity/body @eid))
                                    (assert (grid/valid-position? (:ctx/grid ctx) (:entity/body @eid) (:entity/id @eid))))
                                  (grid/set-touched-cells! (:ctx/grid ctx) eid)
                                  (when (:body/collides? (:entity/body @eid)) ; entity/collides? separate fooziboosh, no 'when' just a callback?
                                    (grid/set-occupied-cells! (:ctx/grid ctx) eid))

                                  nil
                                  ; TODO what should a tx return? nil? ctx?
                                  )
   :tx/unregister-eid           (fn
                                  [{:keys [ctx/content-grid
                                           ctx/entity-ids
                                           ctx/grid]
                                    :as ctx}
                                   eid]
                                  (let [id (:entity/id @eid)]
                                    (assert (contains? @entity-ids id))
                                    (swap! entity-ids dissoc id))
                                  (content-grid/remove-entity! content-grid eid)
                                  (grid/remove-from-touched-cells! grid eid)
                                  (when (:body/collides? (:entity/body @eid))
                                    (grid/remove-from-occupied-cells! grid eid))
                                  nil)
   :tx/state-enter              (fn [_ctx eid [state-k state-v]]
                                  (state/enter [state-k state-v] eid))
   :tx/effect                   (fn [ctx effect-ctx effects]
                                  (mapcat #(effect/handle % effect-ctx ctx)
                                          (filter #(effect/applicable? % effect-ctx) effects)))
   :tx/spawn-alert              (fn
                                  [{:keys [ctx/elapsed-time]} position faction duration]
                                  [[:tx/spawn-effect
                                    position
                                    {:entity/alert-friendlies-after-duration
                                     {:counter (timer/create elapsed-time duration)
                                      :faction faction}}]])
   :tx/spawn-line               (fn
                                  [_ctx {:keys [start end duration color thick?]}]
                                  [[:tx/spawn-effect
                                    start
                                    {:entity/line-render {:thick? thick? :end end :color color}
                                     :entity/delete-after-duration duration}]])
   :tx/move-entity              (fn
                                  [{:keys [ctx/content-grid
                                           ctx/grid]}
                                   eid]
                                  (content-grid/position-changed! content-grid eid)
                                  (grid/remove-from-touched-cells! grid eid)
                                  (grid/set-touched-cells! grid eid)
                                  (when (:body/collides? (:entity/body @eid))
                                    (grid/remove-from-occupied-cells! grid eid)
                                    (grid/set-occupied-cells! grid eid))
                                  nil)
   :tx/spawn-projectile         (fn
                                  [_ctx
                                   {:keys [position direction faction]}
                                   {:keys [entity/image
                                           projectile/max-range
                                           projectile/speed
                                           entity-effects
                                           projectile/size
                                           projectile/piercing?] :as projectile}]
                                  [[:tx/spawn-entity
                                    {:entity/body {:position position
                                                   :width size
                                                   :height size
                                                   :z-order :z-order/flying
                                                   :rotation-angle (v/angle-from-vector direction)}
                                     :entity/movement {:direction direction
                                                       :speed speed}
                                     :entity/image image
                                     :entity/faction faction
                                     :entity/delete-after-duration (/ max-range speed)
                                     :entity/destroy-audiovisual :audiovisuals/hit-wall
                                     :entity/projectile-collision {:entity-effects entity-effects
                                                                   :piercing? piercing?}}]])
   :tx/spawn-effect             (fn [_ctx position components]
                                  [[:tx/spawn-entity
                                    (assoc components
                                           :entity/body {:width 0.5
                                                         :height 0.5
                                                         :z-order :z-order/effect
                                                         :position position})]])
   :tx/spawn-item               (fn [_ctx position item]
                                  [[:tx/spawn-entity
                                    {:entity/body {:position position
                                                   :width 0.75
                                                   :height 0.75
                                                   :z-order :z-order/on-ground}
                                     :entity/image (:entity/image item)
                                     :entity/item item
                                     :entity/clickable {:type :clickable/item
                                                        :text (:property/pretty-name item)}}]])
   :tx/spawn-creature           (fn
                                  [_ctx
                                   {:keys [position
                                           creature-property
                                           components]}]
                                  (assert creature-property)
                                  [[:tx/spawn-entity
                                    (-> creature-property
                                        (assoc :entity/body (let [{:keys [body/width body/height #_body/flying?]} (:entity/body creature-property)]
                                                              {:position position
                                                               :width  width
                                                               :height height
                                                               :collides? true
                                                               :z-order :z-order/ground #_(if flying? :z-order/flying :z-order/ground)}))
                                        (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
                                        (safe-merge components))]])
   :tx/spawn-entity             (fn [ctx entity]
                                  (let [entity (reduce (fn [m [k v]]
                                                         (assoc m k (entity/create [k v] ctx)))
                                                       {}
                                                       entity)
                                        entity (merge (map->Entity {}) entity)
                                        eid (atom entity)]
                                    (cons
                                     [:tx/register-eid eid]
                                     (mapcat #(entity/after-create % eid ctx) @eid))))
   :tx/sound                    (fn [& params] nil)
   :tx/toggle-inventory-visible (fn [& params] nil)
   :tx/show-message             (fn [& params] nil)
   :tx/show-modal               (fn [& params] nil)
   }
  )

(def reaction-txs-fn-map
  {
   :tx/sound                    (fn
                                  [{:keys [ctx/audio] :as ctx} sound-name]
                                  (let [sounds audio]
                                    (assert (contains? sounds sound-name) (str sound-name))
                                    (sound/play! (get sounds sound-name)))
                                  ctx)
   :tx/toggle-inventory-visible (fn
                                  [{:keys [ctx/stage] :as ctx}]
                                  (-> stage
                                      (stage/find-actor "moon.ui.windows.inventory")
                                      actor/toggle-visible!)
                                  ctx)
   :tx/show-message             (fn
                                  [{:keys [ctx/stage] :as ctx} message]
                                  (-> stage
                                      (stage/find-actor "player-message")
                                      (actor/set-user-object! (atom {:text message
                                                                     :counter 0})))
                                  ctx)
   :tx/show-modal               (fn
                                  [{:keys [ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   {:keys [title text button-text on-click]}]
                                  (assert (not (stage/find-actor stage "moon.ui.modal-window")))
                                  (stage/add-actor! stage
                                                    (window/create
                                                     {:title title
                                                      :skin skin
                                                      :window/modal? true
                                                      :table/rows [[{:actor (label/create
                                                                             {:text text
                                                                              :skin skin})}]
                                                                   [{:actor (text-button/create
                                                                             {:text button-text
                                                                              :skin skin
                                                                              :actor/listeners {:listener/change (fn [_event _actor]
                                                                                                                   (actor/remove! (stage/find-actor stage "moon.ui.modal-window"))
                                                                                                                   (on-click))}})}]]
                                                      :actor/name "moon.ui.modal-window"
                                                      :actor/position [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                                                       (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))
                                                                       :align/center]}))
                                  ctx)
   :tx/set-item                 (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid cell item]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                                                          :tooltip-text (info/text item ctx)}
                                                                    skin)))
                                  ctx)
   :tx/remove-item              (fn
                                  [{:keys [ctx/stage] :as ctx} eid cell]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/remove-item! cell)))
                                  ctx)
   :tx/add-skill                (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid skill]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        (stage/find-actor "moon.ui.action-bar")
                                        (action-bar/add-skill! {:skill-id (:property/id skill)
                                                                :texture-region (textures/texture-region textures (:entity/image skill))
                                                                :tooltip-text (info/text skill ctx)}
                                                               skin)))
                                  ctx)
   }
  )

(defn do! [ctx txs]
  (let [handled-txs (try (actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs-fn-map
                     ctx
                     handled-txs)))

(declare draw!)

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/circle! shape-drawer x y radius))
   :draw/ellipse          (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-circle! shape-drawer x y radius))
   :draw/filled-rectangle (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-rectangle! shape-drawer x y w h))
   :draw/grid             (fn
                            [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                            (let [w (* (float gridw) (float cellw))
                                  h (* (float gridh) (float cellh))
                                  topy (+ (float bottomy) (float h))
                                  rightx (+ (float leftx) (float w))]
                              (doseq [idx (range (inc (float gridw)))
                                      :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                (draw! ctx
                                       [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (draw! ctx
                                       [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line             (fn
                            [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/line! shape-drawer sx sy ex ey))
   :draw/rectangle        (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/rectangle! shape-drawer x y w h))
   :draw/sector           (fn
                            [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
   :draw/text             (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/default-font]}
                             {:keys [font scale x y text h-align up?]}]
                            (let [^BitmapFont font (or font default-font)
                                  old-scale (.scaleX (.getData font))
                                  target-width 0
                                  wrap? false
                                  scale (* (float @unit-scale)
                                           (float (or scale 1)))]
                              (.setScale (.getData font) (* old-scale scale))
                              (.draw font
                                     batch
                                     text
                                     (float x)
                                     (float (+ y (if up?
                                                   (-> text
                                                       (str/split #"\n")
                                                       count
                                                       (* (.getLineHeight font)))
                                                   0)))
                                     (float target-width)
                                     Align/center
                                     wrap?)
                              (.setScale (.getData font) old-scale)))
   :draw/texture-region   (fn
                            [{:keys [^SpriteBatch ctx/batch
                                     ctx/unit-scale
                                     ctx/world-unit-scale]}
                             ^TextureRegion texture-region
                             [x y]
                             & {:keys [center? rotation]}]
                            (let [[w h] (let [dimensions [(.getRegionWidth  texture-region)
                                                          (.getRegionHeight texture-region)]]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                              (if center?
                                (.draw batch
                                       texture-region
                                       (- (float x) (/ (float w) 2))
                                       (- (float y) (/ (float h) 2))
                                       (/ (float w) 2)
                                       (/ (float h) 2)
                                       w
                                       h
                                       1
                                       1
                                       (or rotation 0))
                                (.draw batch texture-region (float x) (float y) (float w) (float h)))))
   :draw/with-line-width  (fn
                            [{:keys [ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                              (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                              (draw! ctx draws)
                              (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
   })

(defn draw! [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))

(defn key-pressed? [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))

(defn world-unit-scale [ctx]
  (:ctx/world-unit-scale ctx))

(defn mouse-position [{:keys [ctx/app]}]
  (input/mouse-position (app/input app)))

(defn button-just-pressed? [{:keys [ctx/app]} input-button]
  (input/button-just-pressed? (app/input app) input-button))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
  (let [player-position (:body/position (:entity/body player-entity))
        ; so you cannot put it out of your own reach
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (v/add player-position
           (v/scale (v/direction player-position world-mouse-position)
                    (min maxrange
                         (v/distance player-position world-mouse-position))))))

(defn sound-names [{:keys [ctx/audio]}]
  (map first audio))

(defn key-just-pressed? [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))

(defn player-movement-vector [ctx]
  (let [r (when (key-pressed? ctx input.keys/d) [1  0])
        l (when (key-pressed? ctx input.keys/a) [-1 0])
        u (when (key-pressed? ctx input.keys/w) [0  1])
        d (when (key-pressed? ctx input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v/length v))
          v)))))

(defn- npc-effect-ctx
  [{:keys [ctx/grid
           ctx/raycaster]}
   eid]
  (let [entity @eid
        target (grid/nearest-enemy grid entity)
        target (when (and target
                          (raycaster/line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction (:entity/body entity)
                                                (:entity/body @target)))}))

(defn- npc-choose-skill [ctx entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill/usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect/applicable? e effect-ctx)))
                          (some (fn [e] (effect/useful? e effect-ctx ctx))))))
       first))

(defn- update-effect-ctx
  [raycaster {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (raycaster/line-of-sight? raycaster @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(defn- move-position [position {:keys [direction speed delta-time]}]
  (mapv #(+ %1 (* %2 speed delta-time)) position direction))

(defn- move-body [body movement]
  (update body :body/position move-position movement))

(defn- try-move [grid body entity-id movement]
  (let [new-body (move-body body movement)]
    (when (grid/valid-position? grid new-body entity-id)
      new-body)))

(defn- try-move-solid-body [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move grid body entity-id movement)
        (try-move grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move grid body entity-id (assoc movement :direction [0 ydir])))))

(comment

 ; 1. quote the data structur ebecause of arrows
 ; 2.
 (eval `(fsm/fsm-inc ~data))
 )

(def ^:private npc-fsm
  (fsm/fsm-inc
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
    [:npc-dead]]))

(def ^:private player-fsm
  (fsm/fsm-inc
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
    [:player-dead]]))

(q/defrecord Body [body/position
                   body/width
                   body/height
                   body/collides?
                   body/z-order
                   body/rotation-angle])


(defmethod entity/create :entity/body
  [[_k
    {[x y] :position
     :keys [position
            width
            height
            collides?
            z-order
            rotation-angle]}]
   {:keys [ctx/minimum-size
           ctx/z-orders]}]
  (assert position)
  (assert width)
  (assert height)
  (assert (>= width  (if collides? minimum-size 0)))
  (assert (>= height (if collides? minimum-size 0)))
  (assert (or (boolean? collides?) (nil? collides?)))
  (assert ((set z-orders) z-order))
  (assert (or (nil? rotation-angle)
              (<= 0 rotation-angle 360)))
  (map->Body
   {:position (mapv float position)
    :width  (float width)
    :height (float height)
    :collides? collides?
    :z-order z-order
    :rotation-angle (or rotation-angle 0)}))

(defmethod entity/create :entity/animation
  [[_k {:keys [animation/frames
               animation/frame-duration
               animation/looping?
               delete-after-stopped?]}]
   _ctx]
  (assert (not (and looping? delete-after-stopped?)))
  {:frames (vec frames)
   :frame-duration frame-duration
   :looping? looping?
   :cnt 0
   :maxcnt (* (count frames) (float frame-duration))
   :delete-after-stopped? delete-after-stopped?})

(defmethod entity/create :entity/delete-after-duration
  [[_ duration] {:keys [ctx/elapsed-time]}]
  (timer/create elapsed-time duration))

(defmethod entity/create :entity/stats
  [[_ v] _ctx]
  (stats/create v))

(defmethod entity/create :entity/projectile-collision
  [[_ v] _ctx]
  (assoc v :already-hit-bodies #{}))


(defmethod entity/after-create :entity/fsm ; TODO do @ creature?
  [[_k {:keys [fsm initial-state]}] eid ctx]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  [[:tx/assoc eid :entity/fsm (assoc ((case fsm
                                        :fsms/player player-fsm
                                        :fsms/npc npc-fsm) initial-state nil)
                                     :state initial-state)]
   [:tx/assoc eid initial-state (state/create [initial-state nil] eid ctx)]])

(defmethod entity/after-create :entity/inventory ; TODO do @ creature
  [[_k items] eid _ctx]
  (cons [:tx/assoc eid :entity/inventory (->> inventory/empty-inventory
                                              (map (fn [[slot [width height]]]
                                                     [slot (g2d/create-grid width height (constantly nil))]))
                                              (into {}))]
        (for [item items] ; TODO just call on inventory itself? -> and callback player-refresh ?
          [:tx/pickup-item eid item])))

(defmethod entity/after-create :entity/skills ; TODO same like inventory ?
  [[_k skills] eid _ctx]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))


(defmethod entity/destroy :entity/destroy-audiovisual
  [[_ audiovisuals-id] eid]
  [[:tx/audiovisual
    (:body/position (:entity/body @eid))
    audiovisuals-id]])


(defmethod entity/tick :entity/animation
  [[_k animation] eid {:keys [ctx/delta-time]}]
  [[:tx/assoc eid :entity/animation (animation/tick animation delta-time)]
   (when (and (:delete-after-stopped? animation)
              (animation/stopped? animation))
     [:tx/mark-destroyed eid])])

(defmethod entity/tick :entity/alert-friendlies-after-duration
  [[_k {:keys [counter faction]}]
   eid
   {:keys [ctx/elapsed-time
           ctx/grid]}]
  (when (timer/stopped? elapsed-time counter)
    (cons [:tx/mark-destroyed eid]
          (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                   :radius 4}
                                  (grid/circle->entities grid)
                                  (filter #(= (:entity/faction @%) faction)))]
            [:tx/event friendly-eid :alert]))))

(defmethod entity/tick :entity/delete-after-duration
  [[_k counter] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/mark-destroyed eid]]))

(defmethod entity/tick :entity/movement
  [[_k
    {:keys [direction
            speed
            rotate-in-movement-direction?]
     :as movement}]
   eid
   {:keys [ctx/delta-time
           ctx/grid
           ctx/max-speed]}]
  (assert (<= 0 speed max-speed)
          (pr-str speed))
  (assert (vector? direction))
  (assert (or (zero? (v/length direction))
              (number/nearly-equal? 1 (v/length direction)))
          (str "cannot understand direction: " (pr-str direction)))
  (when-not (or (zero? (v/length direction))
                (nil? speed)
                (zero? speed))
    (let [movement (assoc movement :delta-time delta-time)
          body (:entity/body @eid)]
      (when-let [body (if (:body/collides? body)
                        (try-move-solid-body grid body (:entity/id @eid) movement)
                        (move-body body movement))]
        [[:tx/assoc-in eid [:entity/body :body/position] (:body/position body)]
         (when rotate-in-movement-direction?
           [:tx/assoc-in eid [:entity/body :body/rotation-angle] (v/angle-from-vector direction)])
         [:tx/move-entity eid]]))))

(defmethod entity/tick :entity/skills
  [[_k skills] eid {:keys [ctx/elapsed-time]}]
  (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
        :when (and cooling-down?
                   (timer/stopped? elapsed-time cooling-down?))]
    [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))

(defmethod entity/tick :entity/string-effect
  [[_k {:keys [counter]}]
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/string-effect]]))

(defmethod entity/tick :entity/temp-modifier
  [[_k {:keys [modifiers counter]}]
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/temp-modifier]
     [:tx/update eid :entity/stats stats/remove-mods modifiers]]))

(defmethod entity/tick :entity/projectile-collision
  [[_k {:keys [entity-effects already-hit-bodies piercing?]}]
   eid
   {:keys [ctx/grid]}]
  (let [entity @eid
        cells* (map deref (g2d/get-cells grid (body/touched-tiles (:entity/body entity))))
        hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                        (not= (:entity/faction entity)
                                              (:entity/faction @%))
                                        (:body/collides? (:entity/body @%))
                                        (body/overlaps? (:entity/body entity)
                                                        (:entity/body @%)))
                                  (grid/cells->entities cells*)))
        destroy? (or (and hit-entity (not piercing?))
                     (some #(cell/blocked? % (:body/z-order (:entity/body entity))) cells*))]
    [(when destroy?
       [:tx/mark-destroyed eid])
     (when hit-entity
       [:tx/assoc-in
        eid
        [:entity/projectile-collision
         :already-hit-bodies]
        (conj already-hit-bodies hit-entity)])
     (when hit-entity
       [:tx/effect
        {:effect/source eid
         :effect/target hit-entity}
        entity-effects])]))

(defmethod entity/tick :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
   eid
   {:keys [ctx/elapsed-time
           ctx/raycaster]}]
  (let [effect-ctx (update-effect-ctx raycaster effect-ctx)]
    (cond
     (not (seq (filter #(effect/applicable? % effect-ctx)
                       (:skill/effects skill))))
     [[:tx/event eid :action-done]]

     (timer/stopped? elapsed-time counter)
     [[:tx/effect effect-ctx (:skill/effects skill)]
      [:tx/event eid :action-done]])))

(defmethod entity/tick :npc-idle
  [_ eid ctx]
  (let [effect-ctx (npc-effect-ctx ctx eid)]
    (if-let [skill (npc-choose-skill ctx @eid effect-ctx)]
      [[:tx/event eid :start-action [skill effect-ctx]]]
      [[:tx/event eid :movement-direction (or (grid/find-direction (:ctx/grid ctx) eid)
                                              [0 0])]])))

(defmethod entity/tick :npc-sleeping
  [_ eid {:keys [ctx/grid]}]
  (let [entity @eid]
    (when-let [distance (grid/nearest-enemy-distance grid entity)]
      (when (<= distance (stats/get-stat-value (:entity/stats entity) :stats/aggro-range))
        [[:tx/event eid :alert]]))))

(defmethod entity/tick :npc-moving
  [[_k {:keys [timer]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))

(defmethod entity/tick :stunned
  [[_k {:keys [counter]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))

(defmethod entity/render :entity/clickable
  [[_k {:keys [text]}]
   {:keys [entity/body
           entity/mouseover?]}
   _ctx]
  (when (and mouseover? text)
    (let [[x y] (:body/position body)]
      [[:draw/text {:text text
                    :x x
                    :y (+ y (/ (:body/height body) 2))
                    :up? true}]])))

(defmethod entity/render :entity/image
  [[_k image] {:keys [entity/body]} {:keys [ctx/textures]}]
  [[:draw/texture-region
    (textures/texture-region textures image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(defmethod entity/render :entity/animation
  [[_k animation] entity ctx]
  (entity/render [:entity/image (animation/current-frame animation)]
                 entity
                 ctx))

(defmethod entity/render :entity/line-render
  [[_k {:keys [thick? end color]}]
   {:keys [entity/body]}
   _ctx]
  (let [position (:body/position body)]
    (if thick?
      [[:draw/with-line-width
        4
        [[:draw/line position end color]]]]
      [[:draw/line position end color]])))

(def mouseover-ellipse-width 5)

(defmethod entity/render :entity/mouseover?
  [_
   {:keys [entity/body
           entity/faction]}
   {:keys [ctx/colors
           ctx/player-eid]}]
  (let [player @player-eid]
    [[:draw/with-line-width mouseover-ellipse-width
      [[:draw/ellipse
        (:body/position body)
        (/ (:body/width  body) 2)
        (/ (:body/height body) 2)
        (cond (= faction (faction/enemy (:entity/faction player)))
              (:colors/enemy-color colors)
              (= faction (:entity/faction player))
              (:colors/friendly-color colors)
              :else
              (:colors/neutral-color colors))]]]]))

(defmethod entity/render :entity/stats
  [_ entity {:keys [ctx/colors] :as ctx}]
  (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [body/position body/width body/height]} (:entity/body entity)
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 (world-unit-scale ctx))
            border (* 1 (world-unit-scale ctx))]
        [[:draw/filled-rectangle x y width height (:colors/hp-bar-rect colors)]
         [:draw/filled-rectangle
          (+ x border)
          (+ y border)
          (- (* width ratio) (* 2 border))
          (- height          (* 2 border))
          ((:colors/hp-bar colors) ratio)]]))))

(defmethod entity/render :entity/string-effect
  [[_k {:keys [text]}] entity ctx]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 (world-unit-scale ctx)))
                  :scale 2
                  :up? true}]]))

(defmethod entity/render :entity/temp-modifier
  [_ entity {:keys [ctx/colors]}]
  [[:draw/filled-circle
    (:body/position (:entity/body entity))
    0.5
    (:colors/temp-modifier colors)]])

(def ^:private skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defmethod entity/render :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
   entity
   {:keys [ctx/colors
           ctx/elapsed-time
           ctx/textures]
    :as ctx}]
  (let [{:keys [entity/image skill/effects]} skill]
    (concat (let [action-counter-ratio (timer/ratio elapsed-time counter)
                  texture-region (textures/texture-region textures image)
                  radius skill-image-radius-world-units
                  [x y] (:body/position (:entity/body entity))
                  y (+ (float y)
                       (float (/ (:body/height (:entity/body entity)) 2))
                       (float 0.15))
                  center [x (+ y radius)]]
              [[:draw/filled-circle center radius (:colors/active-skill-circle colors)]
               [:draw/sector
                center
                radius
                (math/to-radians 90) ; start-angle
                (math/to-radians (* (float action-counter-ratio) 360)) ; degree
                (:colors/active-skill-sector colors)]
               [:draw/texture-region texture-region [(- (float x) radius) y]]])
            (mapcat #(effect/render % effect-ctx ctx)  ; update-effect-ctx here too ?
                    effects))))

(defmethod entity/render :npc-sleeping
  [_ {:keys [entity/body]} _ctx]
  (let [[x y] (:body/position body)]
    [[:draw/text {:text "zzz"
                  :x x
                  :y (+ y (/ (:body/height body) 2))
                  :up? true}]]))

(defmethod entity/render :stunned
  [_ {:keys [entity/body]} {:keys [ctx/colors]}]
  [[:draw/circle
    (:body/position body)
    0.5
    (:colors/stunned colors)]])

(defmethod entity/render :player-item-on-cursor
  [[_k {:keys [item]}]
   entity
   {:keys [ctx/stage
           ctx/textures]
    :as ctx}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when-not (stage/mouseover-actor stage (mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (item-place-position ctx entity)
      {:center? true}]]))

(defn- interaction-state->txs [[k params] stage player-eid]
  (case k
    :interaction-state/mouseover-actor nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid
                  in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
             (-> stage
                 (stage/find-actor "moon.ui.windows.inventory")
                 actor/visible?)
             [[:tx/sound "bfxr_takeit"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/event player-eid :pickup-item item]]

             (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
             [[:tx/sound "bfxr_pickup"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/pickup-item player-eid item]]

             :else
             [[:tx/sound "bfxr_denied"]
              [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      [[:tx/event player-eid :start-action [skill effect-ctx]]])

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied"]
     [:tx/show-message "No selected skill"]]))

(defn- creature-speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(def reaction-time-multiplier 0.016)

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (stats/get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))


(defmethod state/create :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier @eid skill)
                 (timer/create elapsed-time))})

(defmethod state/create :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (timer/create elapsed-time duration)})

(defmethod state/create :player-moving
  [[_k movement-vector] eid _ctx]
  {:movement-vector movement-vector})

(defmethod state/create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})

(defmethod state/create :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})


(defmethod state/enter :player-item-on-cursor
  [[_k {:keys [item]}] eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

(defmethod state/enter :active-skill
  [[_k {:keys [skill]}] eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])

(defmethod state/enter :npc-dead
  [_ eid]
  [[:tx/mark-destroyed eid]])

(defmethod state/enter :player-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmethod state/enter :player-dead
  [_ _eid]
  [[:tx/sound "bfxr_playerdeath"]
   [:tx/show-modal {:title "YOU DIED - again!"
                    :text "Good luck next time!"
                    :button-text "OK"
                    :on-click (fn [])}]])

(defmethod state/enter :npc-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])


(defmethod state/exit :player-item-on-cursor
  [_ eid ctx]
  ; at clicked-cell when we put it into a inventory-cell
  ; we do not want to drop it on the ground too additonally,
  ; so we dissoc it there manually. Otherwise it creates another item
  ; on the ground
  (let [entity @eid]
    (when (:entity/item-on-cursor entity)
      [[:tx/sound "bfxr_itemputground"]
       [:tx/dissoc eid :entity/item-on-cursor]
       [:tx/spawn-item
        (item-place-position ctx entity)
        (:entity/item-on-cursor entity)]])))

(defmethod state/exit :player-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defmethod state/exit :npc-sleeping
  [_ eid _ctx]
  [[:tx/spawn-alert (:body/position (:entity/body @eid)) (:entity/faction @eid) 0.2]
   [:tx/add-text-effect eid "[WHITE]!" 1]])

(defmethod state/exit :npc-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])


(defmethod state/cursor :player-item-on-cursor
  [_ _eid _ctx]
  :cursors/hand-grab)

(defmethod state/cursor :player-dead
  [_ _eid _ctx]
  :cursors/black-x)

(defmethod state/cursor :active-skill
  [_ _eid _ctx]
  :cursors/sandclock)

(defmethod state/cursor :stunned
  [_ _eid _ctx]
  :cursors/denied)

(defmethod state/cursor :player-moving
  [_ _eid _ctx]
  :cursors/walking)

(defmethod state/cursor :player-idle
  [_ eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
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
      :cursors/no-skill-selected)))


(defmethod state/pause-game? :active-skill
  [_]
  false)

(defmethod state/pause-game? :stunned
  [_]
  false)

(defmethod state/pause-game? :player-moving
  [_]
  false)

(defmethod state/pause-game? :player-idle
  [_]
  true)

(defmethod state/pause-game? :player-dead
  [_]
  true)

(defmethod state/pause-game? :player-item-on-cursor
  [_]
  true)


(defmethod state/clicked-inventory-cell :default
  [_ _eid _cell]
  nil)

(defmethod state/clicked-inventory-cell :player-idle
  [_ eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    [[:tx/sound "bfxr_takeit"]
     [:tx/event eid :pickup-item item]
     [:tx/remove-item eid cell]]))

(defmethod state/clicked-inventory-cell :player-item-on-cursor
  [_ eid cell]
  (let [entity @eid
        inventory (:entity/inventory entity)
        item-in-cell (get-in inventory cell)
        item-on-cursor (:entity/item-on-cursor entity)]
    (cond
     ; PUT ITEM IN EMPTY CELL
     (and (not item-in-cell)
          (inventory/valid-slot? cell item-on-cursor))
     [[:tx/sound "bfxr_itemput"]
      [:tx/dissoc eid :entity/item-on-cursor]
      [:tx/set-item eid cell item-on-cursor]
      [:tx/event eid :dropped-item]]

     ; STACK ITEMS
     (and item-in-cell
          (inventory/stackable? item-in-cell item-on-cursor))
     [[:tx/sound "bfxr_itemput"]
      [:tx/dissoc eid :entity/item-on-cursor]
      [:tx/stack-item eid cell item-on-cursor]
      [:tx/event eid :dropped-item]]

     ; SWAP ITEMS
     (and item-in-cell
          (inventory/valid-slot? cell item-on-cursor))
     [[:tx/sound "bfxr_itemput"]
      ; need to dissoc and drop otherwise state enter does not trigger picking it up again
      ; TODO? coud handle pickup-item from item-on-cursor state also
      [:tx/dissoc eid :entity/item-on-cursor]
      [:tx/remove-item eid cell]
      [:tx/set-item eid cell item-on-cursor]
      [:tx/event eid :dropped-item]
      [:tx/event eid :pickup-item item-in-cell]])))


(defmethod state/draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]
          :as ctx}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))


(defmethod state/handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/stage]
          :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (mouse-position ctx))]
    (when (and (button-just-pressed? ctx input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))

(defmethod state/handle-input :player-moving
  [_ eid ctx]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))

(defmethod state/handle-input :player-idle
  [_ player-eid {:keys [ctx/interaction-state
                        ctx/stage] :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (button-just-pressed? ctx input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))
