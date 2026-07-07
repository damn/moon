(ns clojure.useful-target-entity
  (:require [clojure.in-range :refer [in-range?]]))

(defn f
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _ctx]
  (in-range? (:entity/body @source)
             (:entity/body @target)
             maxrange))
