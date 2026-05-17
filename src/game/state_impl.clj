(ns game.state-impl
  (:require [moon.state :as state]
            [moon.stats :as stats]
            [moon.timer :as timer]))

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

(defmethod state/create :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})

(def reaction-time-multiplier 0.016)

(defmethod state/create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})
