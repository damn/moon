(ns moon.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.db.build :refer [build]]
            [moon.schemas.validate :refer [validate]]
            [moon.property.type :refer [property->type]]
            [moon.schemas.build-values :refer [build-values]]
            [moon.schemas.create-value :refer [create-value]]))

(defn create []
  (let [schemas (-> "config/schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "config/properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (validate schemas (property->type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))

(defmethod create-value :s/map [_ v db]
  (build-values (:db/schemas db) v db))

(defmethod create-value :s/one-to-many [_ property-ids db]
  (set (map (partial build db) property-ids)))

(defmethod create-value :s/one-to-one [_ property-id db]
  (build db property-id))
