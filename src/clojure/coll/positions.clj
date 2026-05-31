(ns clojure.coll.positions
  (:require [clojure.coll.indexed :as indexed]))

(defn f
  "Returns a lazy sequence containing the positions at which pred
  is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed/f coll) :when (pred elt)] idx))
