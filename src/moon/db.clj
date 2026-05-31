(ns moon.db
  (:require [clojure.core-ext :refer [recur-sort]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [moon.property :as property]
            [moon.schemas :as schemas]))

(defn- save!
  [{:keys [db/data db/file]}]
  (let [data (->> (vals data)
                  (sort-by property/type)
                  (map recur-sort)
                  doall)]
    (.start
     (Thread.
      (fn []
        (binding [*print-level* nil]
          (->> data
               pprint/pprint
               with-out-str
               (spit file))))))))

(defn property-types [{:keys [db/schemas]}]
  (filter #(= "properties" (namespace %)) (keys schemas)))

(defn update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? data id))
  (schemas/validate schemas (property/type property) property)
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
       (filter #(= property-type (property/type %)))))

(defn build [{:keys [db/schemas] :as this} property-id]
  (schemas/build-values schemas
                        (get-raw this property-id)
                        this))

(defn build-all [{:keys [db/schemas] :as this} property-type]
  (map #(schemas/build-values schemas % this)
       (all-raw this property-type)))

(defn create []
  (let [schemas (-> "config/schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "config/properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (schemas/validate schemas (property/type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))

(defmethod schemas/create-value :s/map [_ v db]
  (schemas/build-values (:db/schemas db) v db))

(defmethod schemas/create-value :s/one-to-many [_ property-ids db]
  (set (map (partial build db) property-ids)))

(defmethod schemas/create-value :s/one-to-one [_ property-id db]
  (build db property-id))
