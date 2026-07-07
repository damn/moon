(ns clojure.k-tick.alert-friendlies-after-duration
  (:require [clojure.stopped :refer [stopped?]]
            [clojure.circle-entities :refer [circle->entities]]))

(defn f
  [{:keys [counter faction]}
   eid
   {:keys [ctx/elapsed-time
           ctx/grid]}]
  (when (stopped? elapsed-time counter)
    (cons [:tx/mark-destroyed eid]
          (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                   :radius 4}
                                  (circle->entities grid)
                                  (filter #(= (:entity/faction @%) faction)))]
            [:tx/event friendly-eid :alert]))))
