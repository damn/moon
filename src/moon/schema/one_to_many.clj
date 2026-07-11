(ns moon.schema.one-to-many
  (:require [moon.schema :as schema]
            [clojure.type-id-namespace :refer [type->id-namespace]]))

(defmethod schema/malli-form :s/one-to-many
  [[_ property-type] _]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])
