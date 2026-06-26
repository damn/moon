(ns malli.is-optional
  (:require [malli.map-form-k-properties :refer [map-form-k->properties]]))

(defn f [k map-schema]
  (:optional (k (map-form-k->properties map-schema))))
