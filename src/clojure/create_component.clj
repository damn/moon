(ns clojure.create-component
  (:require [clojure.k-create :refer [k->create]]))

(defn create-component
  [ctx k v]
  (if-let [f (k->create k)]
    (f v ctx)
    v))
