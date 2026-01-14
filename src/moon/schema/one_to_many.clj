(ns moon.schema.one-to-many
  (:require [moon.db :as db]
            [moon.schema :as schema]
            [moon.property :as property]))

(defmethod schema/malli-form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

(defmethod schema/create-value :s/one-to-many [_ property-ids db]
  (set (map (partial db/build db) property-ids)))
