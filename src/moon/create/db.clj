(ns moon.create.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [moon.db :as db]
            [moon.property :as property]
            [moon.map :as map]
            [moon.schemas :as schemas]))

(defn step [ctx {:keys [schemas properties]}]
  (assoc ctx :ctx/db
         (let [schemas (-> schemas io/resource slurp edn/read-string)
               properties-file (io/resource properties)
               properties (-> properties-file slurp edn/read-string)]
           (assert (or (empty? properties)
                       (apply distinct? (map :property/id properties))))
           (doseq [property properties]
             (schemas/validate schemas (property/type property) property))
           {:db/data (zipmap (map :property/id properties) properties)
            :db/file properties-file
            :db/schemas schemas})))

; TODO schema separate ctx key ?

(defn- save!
  [{:keys [db/data db/file]}]
  (let [data (->> (vals data)
                  (sort-by property/type)
                  (map map/recur-sort)
                  doall)]
    (.start
     (Thread.
      (fn []
        (binding [*print-level* nil]
          (->> data
               pprint/pprint
               with-out-str
               (spit file))))))))

(extend-type clojure.lang.PersistentArrayMap
  db/DB
  (property-types [{:keys [db/schemas]}]
    (filter #(= "properties" (namespace %)) (keys schemas)))

  (update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
    (assert (contains? property :property/id))
    (assert (contains? data id))
    (schemas/validate schemas (property/type property) property)
    (let [new-db (update this :db/data assoc id property)]
      (save! new-db)
      new-db))

  (delete! [{:keys [db/data] :as this} property-id]
    (assert (contains? data property-id))
    (let [new-db (update this :db/data dissoc property-id)]
      (save! new-db)
      new-db))

  (get-raw [{:keys [db/data]} property-id]
    (assert (contains? data property-id))
    (get data property-id))

  (all-raw [{:keys [db/data]} property-type]
    (->> (vals data)
         (filter #(= property-type (property/type %)))))

  (build [{:keys [db/schemas] :as this} property-id]
    (schemas/build-values schemas
                          (db/get-raw this property-id)
                          this))

  (build-all [{:keys [db/schemas] :as this} property-type]
    (map #(schemas/build-values schemas % this)
         (db/all-raw this property-type))))
