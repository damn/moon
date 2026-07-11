(ns moon.schema.map
  (:require [moon.schema :as schema]
            [moon.schemas :as schemas]))

(defmethod schema/malli-form :s/map
  [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))
