(ns schema.malli-form.map
  (:require [moon.schemas.create-map-schema :refer [create-map-schema]]))

(defn f [[_ ks] schemas]
  (create-map-schema schemas ks))
