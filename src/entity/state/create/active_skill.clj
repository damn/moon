(ns entity.state.create.active-skill
  (:require [game.state :as state]
            [moon.stats.get-stat-value :refer [get-stat-value]]
            [clojure.timer.create :refer [create-timer]]))

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))

(defmethod state/create :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier @eid skill)
                 (create-timer elapsed-time))})
