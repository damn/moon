(ns moon.tx.add-text-effect
  (:require [moon.timer :as timer]))

(defn do!
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
