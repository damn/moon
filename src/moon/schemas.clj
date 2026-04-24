(ns moon.schemas
  (:require [moon.malli :as m]))

(defmulti create-value (fn [[k] _v _db]
                         k))

(defmethod create-value :default [_ v _db]
  v)

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

(defmulti malli-form (fn [[k] _schemas]
                       k))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      m/schema
      (m/validate-humanize value)))

(defn create-map-schema [schemas ks]
  (m/create-map-schema ks (fn [k]
                            (malli-form (get schemas k) schemas))))

(defn map-keys [schemas schema]
  (m/map-keys (malli-form schema schemas)))

(defn optional? [schemas schema k]
  (m/optional? k (malli-form schema schemas)))

(defn optional-keyset [schemas schema]
  (m/optional-keyset (malli-form schema schemas)))
