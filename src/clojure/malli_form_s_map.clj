(ns clojure.malli-form-s-map
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.schemas :as schemas]))

(defmethod malli-form :s/map
  [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))
