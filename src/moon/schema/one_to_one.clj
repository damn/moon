(ns moon.schema.one-to-one
  (:require [moon.db :as db]
            [moon.schema :as schema]
            [moon.property :as property]))

(defmethod schema/malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defmethod schema/create-value :s/one-to-one [_ property-id db]
  (db/build db property-id))
