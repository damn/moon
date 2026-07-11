(ns moon.schemas
  (:require [moon.schema :as schema]))

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
