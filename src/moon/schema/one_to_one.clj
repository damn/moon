(ns moon.schema.one-to-one
  (:require [moon.malli :as malli]
            [moon.property :as property]))

(defmethod malli/form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])
