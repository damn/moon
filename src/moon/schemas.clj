(ns moon.schemas
  (:require [malli.core :as m]
            [malli.utils :as mu]
            [moon.property :as property]
            [moon.val-max :as val-max]))

(defmulti malli-form (fn [[k] _schemas]
                       k))

(defmulti create-value (fn [[k] _v _db]
                         k))

(defmethod create-value :default [_ v _db]
  v)

(defn create-map-schema [schemas ks]
  (mu/create-map-schema ks (fn [k]
                             (malli-form (get schemas k) schemas))))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      m/schema
      (mu/validate-humanize value)))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(defn default-value [schemas k]
  (let [schema (get schemas k)]
    (cond
     (#{:s/map} (schema 0)) {}
     :else nil)))

(defn optional-keyset [schemas schema]
  (mu/optional-keyset (malli-form schema schemas)))

(defn optional? [schemas schema k]
  (mu/optional? k (malli-form schema schemas)))

(defn map-keys [schemas schema]
  (mu/map-keys (malli-form schema schemas)))

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
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

(defmethod malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defmethod malli-form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))

(defmethod malli-form :s/some [_ _schemas]
  :some)

(defmethod malli-form :s/sound [_ _schemas]
  :string)

(defmethod malli-form :s/string [_ _schemas]
  :string)

(defmethod malli-form :s/val-max [_ _schemas]
  val-max/schema)

(defmethod malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
