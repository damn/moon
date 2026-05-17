(ns game.tx.add-text-effect
  (:require [moon.timer :as timer]))

(defn do! [{:keys [ctx/elapsed-time]} eid text duration]
  [[:tx/assoc
    eid
    :entity/string-effect
    (if-let [existing (:entity/string-effect @eid)]
      (-> existing
          (update :text str "\n" text)
          (update :counter timer/increment duration))
      {:text text
       :counter (timer/create elapsed-time duration)})]])
