(ns moon.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.db.save :refer [save!]]
            [moon.schemas.validate :refer [validate]]
            [moon.property.type :refer [property->type]]
            [moon.schemas.build-values :refer [build-values]]
            [moon.schemas.create-value :refer [create-value]]))

(defn property-types [{:keys [db/schemas]}]
  (filter #(= "properties" (namespace %)) (keys schemas)))

(defn update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? data id))
  (validate schemas (property->type property) property)
  (let [new-db (update this :db/data assoc id property)]
    (save! new-db)
    new-db) )

(defn delete! [{:keys [db/data] :as this} property-id]
  (assert (contains? data property-id))
  (let [new-db (update this :db/data dissoc property-id)]
    (save! new-db)
    new-db))

(defn get-raw [{:keys [db/data]} property-id]
  (assert (contains? data property-id))
  (get data property-id) )

(defn all-raw [{:keys [db/data]} property-type]
  (->> (vals data)
       (filter #(= property-type (property->type %)))))

(defn build [{:keys [db/schemas] :as this} property-id]
  (build-values schemas
                (get-raw this property-id)
                this))

(defn build-all [{:keys [db/schemas] :as this} property-type]
  (map #(build-values schemas % this)
       (all-raw this property-type)))

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
