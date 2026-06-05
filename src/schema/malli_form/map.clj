(ns schema.malli-form.map
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.schemas.create-map-schema :refer [create-map-schema]]))

(defmethod malli-form :s/map [[_ ks] schemas]
  (create-map-schema schemas ks))
