(ns moon.schema
  (:require [moon.property :as property]
            [moon.val-max :as val-max]))

(declare malli-form)

(defn- create-map-schema* [ks k->malli-schema-form]
  (apply vector :map {:closed true}
         (for [k ks
               :let [k? (keyword? k)
                     schema-props (if k? nil (k 1))
                     k (if k? k (k 0))]]
           (do
            (assert (keyword? k))
            (assert (or (nil? schema-props) (map? schema-props)) (pr-str ks))
            [k schema-props (k->malli-schema-form k)]))))

(defn create-map-schema [schemas ks]
  (create-map-schema* ks
                      (fn [k]
                        (malli-form (get schemas k) schemas))))

(defn malli-form [schema schemas]
  (case (first schema)
    :s/animation
    (create-map-schema schemas
                       [:animation/frames
                        :animation/frame-duration
                        :animation/looping?])

    :s/boolean
    :boolean

    :s/enum
    (apply vector :enum (rest schema))

    :s/image
    (create-map-schema schemas
                       [:image/file
                        [:image/bounds {:optional true}]])

    :s/map
    (create-map-schema schemas (second schema))

    :s/number
    (case (second schema)
      :int     int?
      :nat-int nat-int?
      :any     number?
      :pos     pos?
      :pos-int pos-int?)

    :s/one-to-many
    [:set [:qualified-keyword {:namespace (property/type->id-namespace (second schema))}]]

    :s/one-to-one
    [:qualified-keyword {:namespace (property/type->id-namespace (second schema))}]

    :s/qualified-keyword
    (apply vector :qualified-keyword (rest schema))

    :s/some
    :some

    :s/sound
    :string

    :s/string
    :string

    :s/val-max
    val-max/schema

    :s/vector
    (apply vector :vector (rest schema))))
