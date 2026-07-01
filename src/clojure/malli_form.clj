(ns clojure.malli-form
  (:require [malli.create-map-schema :as create-map-schema]
            [moon.property.type-id-namespace :refer [type->id-namespace]]
            [moon.val-max.schema :as schema]))

(defmulti malli-form (fn [[k] _schemas]
                       k))

(defn create-map-schema [schemas ks]
  (create-map-schema/f ks (fn [k]
                            (malli-form (get schemas k) schemas))))

(defmethod malli-form :s/animation [_ schemas]
  (create-map-schema schemas
                     [:animation/frames
                      :animation/frame-duration
                      :animation/looping?]))

(defmethod malli-form :s/boolean [_ _schemas]
  :boolean)

(defmethod malli-form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))

(defmethod malli-form :s/image [_ schemas]
  (create-map-schema schemas
                     [:image/file
                      [:image/bounds {:optional true}]]))

(defmethod malli-form :s/map [[_ ks] schemas]
  (create-map-schema schemas ks))

(defmethod malli-form :s/number [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defmethod malli-form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (type->id-namespace property-type)}]])

(defmethod malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (type->id-namespace property-type)}])

(defmethod malli-form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))

(defmethod malli-form :s/some [_ _schemas]
  :some)

(defmethod malli-form :s/sound [_ _schemas]
  :string)

(defmethod malli-form :s/string [_ _schemas]
  :string)

(defmethod malli-form :s/val-max [_ _schemas]
  schema/v)

(defmethod malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
