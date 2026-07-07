(ns clojure.tx.state-exit
  (:require [clojure.k-state-exit :refer [k->state-exit]]))

(defn do!
  [ctx eid [state-k state-v]]
  (if-let [f (k->state-exit state-k)]
    (f state-v eid ctx)
    nil))
