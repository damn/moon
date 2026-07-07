(ns clojure.render-target-entity
  (:require [clojure.in-range :refer [in-range?]]
            [clojure.start-point :refer [start-point]]
            [clojure.end-point :refer [end-point]]))

(defn f
  [[_ {:keys [maxrange]}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/colors]}]
  (when target
    (let [body        (:entity/body @source)
          target-body (:entity/body @target)]
      [[:draw/line
        (start-point body target-body)
        (end-point body target-body maxrange)
        (if (in-range? body target-body maxrange)
          (:colors/target-entity-in-range colors)
          (:colors/target-entity-not-in-range colors))]])))
