(ns schema.malli-form.one-to-one
  (:require [moon.property.type-id-namespace :refer [type->id-namespace]]))

(defn f [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])
