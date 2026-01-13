(ns moon.txs-impl
  (:require [clojure.math.vector2 :as v]
            [malli.utils :as mu]
            [moon.audio :as audio]
            [moon.body :as body]
            [moon.ctx]
            [moon.db :as db]
            [moon.effect :as effect]
            [moon.entity.skills :as skills]
            [moon.entity.state :as state]
            [moon.entity.stats :as stats]
            [moon.graphics :as graphics]
            [moon.inventory :as inventory]
            [moon.timer :as timer]
            [moon.ui :as ui]
            [moon.utils :as utils]
            [moon.world :as world]
            [moon.world.content-grid :as content-grid]
            [moon.world.grid :as grid]
            [moon.world.info :as info]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (moon Stage)))

(def reaction-txs-fn-map
  {
   :tx/sound                    (fn
                                  [{:keys [ctx/audio] :as ctx} sound-name]
                                  (audio/play! audio sound-name)
                                  ctx)
   :tx/toggle-inventory-visible (fn
                                  [{:keys [ctx/stage] :as ctx}]
                                  (ui/toggle-inventory-visible! stage)
                                  ctx)
   :tx/show-message             (fn
                                  [{:keys [ctx/stage] :as ctx} message]
                                  (ui/show-text-message! stage message)
                                  ctx)
   :tx/show-modal               (fn
                                  [{:keys [ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   opts]
                                  (ui/show-modal-window! stage skin (Stage/.getViewport stage) opts)
                                  ctx)
   :tx/set-item                 (fn
                                  [{:keys [ctx/graphics
                                           ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   eid cell item]
                                  (when (:entity/player? @eid)
                                    (ui/set-item! stage cell
                                                  {:texture-region (graphics/texture-region graphics (:entity/image item))
                                                   :tooltip-text (info/text item nil)}
                                                  skin))
                                  ctx)
   :tx/remove-item              (fn
                                  [{:keys [ctx/stage] :as ctx} eid cell]
                                  (when (:entity/player? @eid)
                                    (ui/remove-item! stage cell))
                                  ctx)
   :tx/add-skill                (fn
                                  [{:keys [ctx/graphics
                                           ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   eid skill]
                                  (when (:entity/player? @eid)
                                    (ui/add-skill! stage
                                                   {:skill-id (:property/id skill)
                                                    :texture-region (graphics/texture-region graphics (:entity/image skill))
                                                    :tooltip-text (fn [{:keys [ctx/world]}]
                                                                    (info/text skill world))}
                                                   skin))
                                  ctx)
   })

(defn- world-move-entity
  [{:keys [world/content-grid
           world/grid]}
   eid body direction rotate-in-movement-direction?]
  (content-grid/position-changed! content-grid eid)
  (grid/remove-from-touched-cells! grid eid)
  (grid/set-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/remove-from-occupied-cells! grid eid)
    (grid/set-occupied-cells! grid eid))
  (swap! eid assoc-in [:entity/body :body/position] (:body/position body))
  (when rotate-in-movement-direction?
    (swap! eid assoc-in [:entity/body :body/rotation-angle] (v/angle-from-vector direction)))
  nil)

(defn- world-handle-event
  ([world eid event]
   (world-handle-event world eid event nil))
  ([world eid event params]
   (let [fsm (:entity/fsm @eid)
         _ (assert fsm)
         old-state-k (:state fsm)
         new-fsm (fsm/fsm-event fsm event)
         new-state-k (:state new-fsm)]
     (when-not (= old-state-k new-state-k)
       (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
             new-state-obj [new-state-k (state/create [new-state-k params] eid world)]]
         [[:tx/assoc       eid :entity/fsm new-fsm]
          [:tx/assoc       eid new-state-k (new-state-obj 1)]
          [:tx/dissoc      eid old-state-k]
          [:tx/state-exit  eid old-state-obj]
          [:tx/state-enter eid new-state-obj]])))))

(defn- create-component [[k v] {:keys [world/create-fns] :as world}]
  (if-let [f (create-fns k)]
    (f v world)
    v))

(defn- after-create-component [[k v] eid {:keys [world/after-create-fns] :as world}]
  (when-let [f (after-create-fns k)]
    (f v eid world)))

(q/defrecord Entity [entity/body])

(defn- world-spawn-entity
 [{:keys [world/content-grid
          world/entity-ids
          world/grid
          world/id-counter
          world/spawn-entity-schema]
   :as world}
  entity]
 (mu/validate-humanize spawn-entity-schema entity)
 (let [entity (reduce (fn [m [k v]]
                        (assoc m k (create-component [k v] world)))
                      {}
                      entity)
       _ (assert (and (not (contains? entity :entity/id))))
       entity (assoc entity :entity/id (swap! id-counter inc))
       entity (merge (map->Entity {}) entity)
       eid (atom entity)]
   (let [id (:entity/id @eid)]
     (assert (number? id))
     (swap! entity-ids assoc id eid))
   (content-grid/add-entity! content-grid eid)
   ; https://github.com/damn/core/issues/58
   ;(assert (valid-position? grid @eid))
   (grid/set-touched-cells! grid eid)
   (when (:body/collides? (:entity/body @eid))
     (grid/set-occupied-cells! grid eid))
   (mapcat #(after-create-component % eid world) @eid)))

; TODO just call fns? no need 'transactions'??
; or only 1 'game/sprite-batch' ? moon.sprite-batch ?

(def txs-fn-map
  {
   ;; FIXME only this passes ctx, otherwise 'world' only
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
   ;;

   :tx/assoc                    (fn
                                  [_ctx eid k value]
                                  (swap! eid assoc k value)
                                  nil)

   :tx/assoc-in                 (fn
                                  [_ctx eid ks value]
                                  (swap! eid assoc-in ks value)
                                  nil)

   :tx/dissoc                   (fn
                                  [_ctx eid k]
                                  (swap! eid dissoc k)
                                  nil)

   :tx/update                   (fn
                                  [_ctx eid & params]
                                  (apply swap! eid update params)
                                  nil)

   :tx/mark-destroyed           (fn
                                  [_ctx eid]
                                  (swap! eid assoc :entity/destroyed? true)
                                  nil)

   :tx/set-cooldown             (fn
                                  [{:keys [ctx/world]} eid skill]
                                  (swap! eid update :entity/skills skills/set-cooldown skill (:world/elapsed-time world))
                                  nil)

   :tx/add-text-effect          (fn
                                  [{:keys [ctx/world]} eid text duration]
                                  [[:tx/assoc
                                    eid
                                    :entity/string-effect
                                    (if-let [existing (:entity/string-effect @eid)]
                                      (-> existing
                                          (update :text str "\n" text)
                                          (update :counter timer/increment duration))
                                      {:text text
                                       :counter (timer/create (:world/elapsed-time world) duration)})]])

   :tx/add-skill                (fn
                                  [_ctx eid {:keys [property/id] :as skill}]
                                  {:pre [(not (contains? (:entity/skills @eid) id))]}
                                  (swap! eid update :entity/skills assoc id skill)
                                  nil)

   :tx/set-item                 (fn
                                  [_ctx eid cell item]
                                  (let [entity @eid
                                        inventory (:entity/inventory entity)]
                                    (assert (and (nil? (get-in inventory cell))
                                                 (inventory/valid-slot? cell item)))
                                    (swap! eid assoc-in (cons :entity/inventory cell) item)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/add (:stats/modifiers item)))
                                    nil))

   :tx/remove-item              (fn
                                  [_ctx eid cell]
                                  (let [entity @eid
                                        item (get-in (:entity/inventory entity) cell)]
                                    (assert item)
                                    (swap! eid assoc-in (cons :entity/inventory cell) nil)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
                                    nil))
   :tx/pickup-item              (fn
                                  [_ctx eid item]
                                  (inventory/assert-valid-item? item)
                                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
                                    (assert cell)
                                    (assert (or (inventory/stackable? item cell-item)
                                                (nil? cell-item)))
                                    (if (inventory/stackable? item cell-item)
                                      (do
                                       #_(tx/stack-item ctx eid cell item))
                                      [[:tx/set-item eid cell item]])))
   :tx/event                    (fn
                                  [{:keys [ctx/world]} & params]
                                  (apply world-handle-event world params))
   :tx/state-enter              (fn [_ctx eid [state-k state-v]]
                                  (state/enter [state-k state-v] eid))
   :tx/effect                   (fn [{:keys [ctx/world]} effect-ctx effects]
                                  (mapcat #(effect/handle % effect-ctx world)
                                          (filter #(effect/applicable? % effect-ctx) effects)))
   :tx/spawn-alert              (fn
                                  [{:keys [ctx/world]} position faction duration]
                                  [[:tx/spawn-effect
                                    position
                                    {:entity/alert-friendlies-after-duration
                                     {:counter (timer/create (:world/elapsed-time world) duration)
                                      :faction faction}}]])
   :tx/spawn-line               (fn
                                  [_ctx {:keys [start end duration color thick?]}]
                                  [[:tx/spawn-effect
                                    start
                                    {:entity/line-render {:thick? thick? :end end :color color}
                                     :entity/delete-after-duration duration}]])
   :tx/move-entity              (fn
                                  [{:keys [ctx/world]} & params]
                                  (apply world-move-entity world params))
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
   :tx/spawn-effect             (fn
                                  [{:keys [ctx/world]}
                                   position
                                   components]
                                  [[:tx/spawn-entity
                                    (assoc components
                                           :entity/body (assoc (:world/effect-body-props world) :position position))]])

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
                                        (utils/safe-merge components))]])
   :tx/spawn-entity             (fn [{:keys [ctx/world]} entity]
                                  (world-spawn-entity world entity))
   :tx/sound                    (fn [& params] nil)
   :tx/toggle-inventory-visible (fn [& params] nil)
   :tx/show-message             (fn [& params] nil)
   :tx/show-modal               (fn [& params] nil)
   })

(.bindRoot #'moon.ctx/reaction-txs-fn-map reaction-txs-fn-map)
(.bindRoot #'moon.ctx/txs-fn-map txs-fn-map)
