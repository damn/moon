(ns moon.schemas
  (:require [moon.malli :as malli]
            [moon.schema :as schema]
            [moon.map-schema :as map-schema]))

(defn create-map-schema*
  "Can define keys as just keywords or with schema-props like [:foo {:optional true}]."
  [ks k->malli-schema-form]
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
                        (schema/malli-form (get schemas k) schemas))))

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
  (-> (get schemas k)
      (schema/malli-form schemas)
      malli/create
      (malli/validate-humanize value)))
