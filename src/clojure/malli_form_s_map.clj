(ns clojure.malli-form-s-map
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli-form-create-map-schema :refer [create-map-schema]]))

(defmethod malli-form :s/map
  [[_ ks] schemas]
  (create-map-schema schemas ks))
