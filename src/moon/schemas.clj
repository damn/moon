(ns moon.schemas
  (:require [malli.core :as m]
            [malli.utils :as mu]
            [malli.utils.validate-humanize :refer [validate-humanize]]))

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
      (validate-humanize value)))

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
