(ns clojure.tx.state-enter
  (:require [clojure.k-state-enter :refer [k->state-enter]]))

(defn do!
  [ctx eid [state-k state-v]]
  (if-let [f (k->state-enter state-k)]
    (f state-v eid)
    nil))
