(ns schema.malli-form.one-to-many
  (:require [moon.property.type-id-namespace :refer [type->id-namespace]]))

(defn f [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])
