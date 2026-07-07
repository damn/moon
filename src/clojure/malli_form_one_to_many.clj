(ns clojure.malli-form-one-to-many
  (:require [clojure.type-id-namespace :refer [type->id-namespace]]))

(defn f [[_ property-type] _]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])
