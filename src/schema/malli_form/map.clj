(ns schema.malli-form.map
  (:require [moon.schemas.malli-form :refer [malli-form]]
            [moon.schemas :refer [create-map-schema]]))

(defmethod malli-form :s/map [[_ ks] schemas]
  (create-map-schema schemas ks))
