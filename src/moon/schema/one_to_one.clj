(ns moon.schema.one-to-one
  (:require [moon.schema :as schema]
            [clojure.type-id-namespace :refer [type->id-namespace]]))

(defmethod schema/malli-form :s/one-to-one
  [[_ property-type] _]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])
