(ns moon.schemas.create-value.one-to-one
  (:require [moon.db.build :refer [build]]))

(defn f [_ property-id db]
  (build db property-id))
