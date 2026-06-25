(ns moon.schemas.create-value.one-to-many
  (:require [moon.db.build :refer [build]]))

(defn f [_ property-ids db]
  (set (map (partial build db) property-ids)))
