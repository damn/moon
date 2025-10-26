(ns moon.entity.state-impl
  (:require [moon.effect :as effect]
            [moon.entity.state :as state]
            [moon.entity.state.player-item-on-cursor :as item-on-cursor]
            [moon.entity.stats :as stats]
            [moon.timer :as timer]))

(def reaction-time-multiplier 0.016)

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (stats/get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))

(def ^:private fn->k->var
  {:create {:active-skill          (fn [eid [skill effect-ctx] {:keys [world/elapsed-time]}]
                                     {:skill skill
                                      :effect-ctx effect-ctx
                                      :counter (->> skill
                                                    :skill/action-time
                                                    (apply-action-speed-modifier @eid skill)
                                                    (timer/create elapsed-time))})
            :npc-moving            (fn [eid movement-vector {:keys [world/elapsed-time]}]
                                     {:movement-vector movement-vector
                                      :timer (timer/create elapsed-time
                                                           (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                                                              reaction-time-multiplier))})
            :player-item-on-cursor (fn [_eid item _world]
                                     {:item item})
            :player-moving         (fn [eid movement-vector _world]
                                     {:movement-vector movement-vector})

            :stunned               (fn [_eid duration {:keys [world/elapsed-time]}]
                                     {:counter (timer/create elapsed-time duration)})}

   :enter {:npc-dead              (fn [_ eid]
                                    [[:tx/mark-destroyed eid]])
           :npc-moving            (fn [{:keys [movement-vector]} eid]
                                    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                                                      :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                                                                 0)}]])
           :player-dead           (fn [_ _eid]
                                    [[:tx/sound "bfxr_playerdeath"]
                                     [:tx/show-modal {:title "YOU DIED - again!"
                                                      :text "Good luck next time!"
                                                      :button-text "OK"
                                                      :on-click (fn [])}]])
           :player-item-on-cursor (fn [{:keys [item]} eid]
                                    [[:tx/assoc eid :entity/item-on-cursor item]])
           :player-moving         (fn [{:keys [movement-vector]} eid]
                                    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                                                      :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                                                                 0)}]])
           :active-skill          (fn [{:keys [skill]} eid]
                                    [[:tx/sound (:skill/start-action-sound skill)]
                                     [:tx/set-cooldown eid skill]
                                     [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])}

   :exit {:npc-moving            (fn [_ eid _ctx]
                                   [[:tx/dissoc eid :entity/movement]])
          :npc-sleeping          (fn [_ eid _ctx]
                                   [[:tx/spawn-alert (:body/position (:entity/body @eid)) (:entity/faction @eid) 0.2]
                                    [:tx/add-text-effect eid "[WHITE]!" 1]])
          :player-item-on-cursor (fn [_ eid {:keys [ctx/graphics]}]
                                   ; at clicked-cell when we put it into a inventory-cell
                                   ; we do not want to drop it on the ground too additonally,
                                   ; so we dissoc it there manually. Otherwise it creates another item
                                   ; on the ground
                                   (let [entity @eid]
                                     (when (:entity/item-on-cursor entity)
                                       [[:tx/sound "bfxr_itemputground"]
                                        [:tx/dissoc eid :entity/item-on-cursor]
                                        [:tx/spawn-item
                                         (item-on-cursor/item-place-position
                                          (:graphics/world-mouse-position graphics)
                                          entity)
                                         (:entity/item-on-cursor entity)]])))
          :player-moving         (fn [_ eid _ctx]
                                   [[:tx/dissoc eid :entity/movement]])}})

(extend clojure.lang.APersistentVector
  state/State
  {:create (fn [[k v] eid ctx]
             (if-let [f (k (:create fn->k->var))]
               (f eid v ctx)
               v))

   :enter (fn [[k v] eid]
            (when-let [f (k (:enter fn->k->var))]
              (f v eid)))

   :exit (fn [[k v] eid ctx]
           (when-let [f (k (:exit fn->k->var))]
             (f v eid ctx)))})
