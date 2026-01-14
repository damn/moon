(ns moon.schema.one-to-many
  (:require [moon.malli :as malli]
            [moon.property :as property]))

(defmethod malli/form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])
