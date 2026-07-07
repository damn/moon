(ns clojure.malli-form-one-to-one
  (:require [clojure.type-id-namespace :refer [type->id-namespace]]))

(defn f [[_ property-type] _]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])
