(ns clojure.create-value-one-to-many
  (:require [clojure.build :refer [build]]))

(defn f [_ property-ids db]
  (set (map (partial build db) property-ids)))
