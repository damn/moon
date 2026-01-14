(ns moon.tx.set-cooldown
  (:require [moon.timer :as timer]))

(defn do!
  [{:keys [ctx/world]} eid skill]
  (swap! eid assoc-in [:entity/skills
                       (:property/id skill)
                       :skill/cooling-down?]
         (timer/create (:world/elapsed-time world) (:skill/cooldown skill)))
  nil)
