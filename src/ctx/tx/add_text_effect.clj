(ns ctx.tx.add-text-effect
  (:require [clojure.timer.increment :as increment]
            [clojure.timer.create :refer [create-timer]]))

(defn do! [{:keys [ctx/elapsed-time]} eid text duration]
  [[:tx/assoc
    eid
    :entity/string-effect
    (if-let [existing (:entity/string-effect @eid)]
      (-> existing
          (update :text str "\n" text)
          (update :counter increment/f duration))
      {:text text
       :counter (create-timer elapsed-time duration)})]])
