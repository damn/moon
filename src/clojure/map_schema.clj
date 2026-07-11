(ns clojure.map-schema
  (:require [clojure.map-form-k-properties :refer [map-form-k->properties]]))

(defn optional? [map-schema k]
  (:optional (k (map-form-k->properties map-schema))))
