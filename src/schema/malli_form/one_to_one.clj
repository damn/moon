(ns schema.malli-form.one-to-one
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.property.type-id-namespace :refer [type->id-namespace]]))

(defmethod malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])
