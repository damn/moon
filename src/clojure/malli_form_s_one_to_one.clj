(ns clojure.malli-form-s-one-to-one
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.type-id-namespace :refer [type->id-namespace]]))

(defmethod malli-form :s/one-to-one
  [[_ property-type] _]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])
