(ns clojure.tick-component
  (:require [clojure.k-tick :refer [k->tick]]))

(defn tick-component
  [ctx eid [k v]]
  (if-let [f (k->tick k)]
    (f v eid ctx)
    nil))
