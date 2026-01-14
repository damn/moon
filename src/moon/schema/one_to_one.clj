(ns moon.schema.one-to-one
  (:require [moon.schema :as schema]
            [moon.property :as property]))

(defmethod schema/malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])
