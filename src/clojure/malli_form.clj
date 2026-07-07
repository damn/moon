(ns clojure.malli-form
  (:require [clojure.malli-create-map-schema :as malli-create-map-schema]
            [clojure.schema :as schema]
            [clojure.type-id-namespace :refer [type->id-namespace]]))

(defmulti malli-form (fn [schema _schemas]
                       (first schema)))

(defn- create-map-schema [schemas ks]
  (malli-create-map-schema/f ks
                               (fn [k]
                                 (malli-form (get schemas k) schemas))))

(defmethod malli-form :s/animation
  [_ schemas]
  (create-map-schema schemas
                     [:animation/frames
                      :animation/frame-duration
                      :animation/looping?]))

(defmethod malli-form :s/boolean
  [_ _]
  :boolean)

(defmethod malli-form :s/enum
  [[_ & params] _]
  (apply vector :enum params))

(defmethod malli-form :s/image
  [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

(defmethod malli-form :s/map
  [[_ ks] schemas]
  (create-map-schema schemas ks))

(defmethod malli-form :s/number
  [[_ predicate] _]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defmethod malli-form :s/one-to-many
  [[_ property-type] _]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])

(defmethod malli-form :s/one-to-one
  [[_ property-type] _]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])

(defmethod malli-form :s/qualified-keyword
  [[_ & params] _]
  (apply vector :qualified-keyword params))

(defmethod malli-form :s/some
  [_ _]
  :some)

(defmethod malli-form :s/sound
  [_ _]
  :string)

(defmethod malli-form :s/string
  [_ _]
  :string)

(defmethod malli-form :s/val-max
  [_ _]
  schema/v)

(defmethod malli-form :s/vector
  [[_ & params] _]
  (apply vector :vector params))
