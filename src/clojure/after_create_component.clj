(ns clojure.after-create-component
  (:require [clojure.k-after-create :refer [k->after-create]]))

(defn after-create-component
  [ctx eid [k v]]
  (if-let [f (k->after-create k)]
    (f v eid ctx)
    nil))
