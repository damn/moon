(ns clojure.is-optional
  (:require [clojure.map-form-k-properties :refer [map-form-k->properties]]))

(defn f [k map-schema]
  (:optional (k (map-form-k->properties map-schema))))
