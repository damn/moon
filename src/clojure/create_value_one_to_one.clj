(ns clojure.create-value-one-to-one
  (:require [clojure.build :refer [build]]))

(defn f [_ property-id db]
  (build db property-id))
