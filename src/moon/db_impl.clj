(ns moon.db-impl
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.db :as db]
            [moon.property :as property]
            [moon.schema :as schema]
            [moon.schemas :as schemas]
            [moon.utils :as utils]))

(defrecord Schemas []
  schemas/Schemas
  (build-values [schemas property db]
    (utils/apply-kvs property
                     (fn [k v]
                       (try (schema/create-value (get schemas k) v db)
                            (catch Throwable t
                              (throw (ex-info " " {:k k :v v} t)))))))

  (default-value [schemas k]
    (let [schema (get schemas k)]
      (cond
       (#{:s/map} (schema 0)) {}
       :else nil)))

  (validate [schemas k value]
    (-> (get schemas k)
        (schema/malli-form schemas)
        m/schema
        (mu/validate-humanize value)))

  (create-map-schema [schemas ks]
    (mu/create-map-schema ks (fn [k]
                               (schema/malli-form (get schemas k) schemas)))))

(defn create []
  (let [schemas (-> "schema.edn" io/resource slurp edn/read-string)
        schemas (map->Schemas schemas)
        properties-file (io/resource "properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (schemas/validate schemas (property/type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))
