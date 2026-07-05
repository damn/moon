(ns moon.schemas.malli-form.one-to-many
  (:require [moon.property.type-id-namespace :refer [type->id-namespace]]))

(defn f [[_ property-type] _]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])
