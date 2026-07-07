(ns ctx.tx.set-cooldown
  (:require [clojure.timer-create :refer [create-timer]]))

(defn do! [{:keys [ctx/elapsed-time]} eid skill]
  (swap! eid assoc-in [:entity/skills
                       (:property/id skill)
                       :skill/cooling-down?]
         (create-timer elapsed-time (:skill/cooldown skill)))
  nil)
