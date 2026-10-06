(ns moon.schemas
  (:require [malli.core :as m]
            [malli.error :as me]
            [moon.schema :as schema]
            [moon.map-schema :as map-schema]))

(defn create-map-schema [schemas ks]
  (schema/create-map-schema schemas ks))

(defn default-value [schemas k]
  (let [schema (get schemas k)]
    (cond
     (#{:s/map} (schema 0)) {}
     :else nil)))

(defn map-keys [schemas schema]
  (map-schema/map-keys (schema/malli-form schema schemas)))

(defn optional-keyset [schemas schema]
  (map-schema/optional-keyset (schema/malli-form schema schemas)))

(defn optional? [schemas schema k]
  (map-schema/optional? (schema/malli-form schema schemas) k))

(defn validate [schemas k value]
  (let [schema (m/schema (schema/malli-form (get schemas k) schemas))]
    (when-not (m/validate schema value)
      (throw (ex-info (str (me/humanize (m/explain schema value)))
                      {:value value
                       :schema (m/form schema)})))))
