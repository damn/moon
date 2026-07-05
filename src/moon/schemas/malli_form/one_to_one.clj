(ns moon.schemas.malli-form.one-to-one
  (:require [moon.property.type-id-namespace :refer [type->id-namespace]]))

(defn f [[_ property-type] _]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])
