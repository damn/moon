(ns moon.malli
  (:require [moon.property :as property]
            [moon.schemas :as schemas]
            [moon.val-max :as val-max]))

(defmulti form (fn [[k] _schemas]
                 k))

(defmethod form :s/animation [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

(defmethod form :s/boolean [_ _schemas]
  :boolean)

(defmethod form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))

(defmethod form :s/image [_ schemas]
  (schemas/create-map-schema schemas
                             [:image/file
                              [:image/bounds {:optional true}]]))

(defmethod form :s/map [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))

(defmethod form :s/number [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defmethod form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

(defmethod form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defmethod form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))

(defmethod form :s/some [_ _schemas]
  :some)

(defmethod form :s/sound [_ _schemas]
  :string)

(defmethod form :s/string [_ _schemas]
  :string)

(defmethod form :s/val-max [_ _schemas]
  val-max/schema)

(defmethod form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
