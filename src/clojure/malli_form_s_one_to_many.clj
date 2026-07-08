(ns clojure.malli-form-s-one-to-many
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.type-id-namespace :refer [type->id-namespace]]))

(defmethod malli-form :s/one-to-many
  [[_ property-type] _]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])
