(ns moon.schema.map
  (:require [moon.malli :as malli]
            [moon.schemas :as schemas]))

(defmethod malli/form :s/map [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))
