(ns tx.load
  (:require [clojure.core-ext :refer [safe-merge]]
            [clojure.math.vector2 :as v]
            [game.ctx]
            [game.effect :as effect]
            [game.entity :as entity]
            [game.state :as state]
            [moon.body :as body]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.grid :as grid]
            [moon.inventory :as inventory]
            [moon.stats :as stats]
            [moon.timer :as timer]
            [reduce-fsm :as fsm]
            [qrecord.core :as q]))

(q/defrecord Entity [entity/body])

(.bindRoot #'game.ctx/txs-fn-map
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
