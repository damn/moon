(ns moon.db
  (:require [clojure.pprint :as pprint]
            [moon.schemas :as schemas]
            [moon.property :as property]
            [moon.map :as map]))

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

(defn property-types [{:keys [db/schemas]}]
  (filter #(= "properties" (namespace %)) (keys schemas)))

(defn update! [{:keys [db/data db/schemas]
                :as this}
               {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? data id))
  (schemas/validate schemas (property/type property) property)
  (let [new-db (update this :db/data assoc id property)]
    (save! new-db)
    new-db))

(defn delete! [{:keys [db/data] :as this} property-id]
  (assert (contains? data property-id))
  (let [new-db (update this :db/data dissoc property-id)]
    (save! new-db)
    new-db))

(defn get-raw [{:keys [db/data]} property-id]
  {:pre [(contains? data property-id)]}
  (get data property-id))

(defn all-raw [{:keys [db/data]} property-type]
  (->> (vals data)
       (filter #(= property-type (property/type %)))))

(defn build
  [{:keys [db/schemas]
    :as this}
   property-id]
  (schemas/build-values schemas
                        (get-raw this property-id)
                        this))

(defn build-all
  [{:keys [db/schemas]
    :as this}
   property-type]
  (map #(schemas/build-values schemas % this)
       (all-raw this property-type)))
