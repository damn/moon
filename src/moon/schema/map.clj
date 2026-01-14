(ns moon.schema.map
  (:require [moon.schema :as schema]
            [moon.schemas :as schemas]))

(defmethod schema/malli-form :s/map [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))

(defmethod schema/create-value :s/map [_ v db]
  (schemas/build-values (:db/schemas db) v db))
