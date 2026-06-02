(ns schema.malli-form.map
  (:require [moon.schemas :refer [malli-form create-map-schema]]))

(defmethod malli-form :s/map [[_ ks] schemas]
  (create-map-schema schemas ks))
