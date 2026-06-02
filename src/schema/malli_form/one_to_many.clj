(ns schema.malli-form.one-to-many
  (:require [moon.schemas :refer [malli-form]]
            [moon.property.type-id-namespace :refer [type->id-namespace]]))

(defmethod malli-form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])
