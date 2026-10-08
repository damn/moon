(ns moon.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [malli.core :as m]
            [malli.error :as me]
            [moon.m :refer [recur-sort]]
            [moon.schema :as schema]))

(defn create []
  (let [schemas (-> "schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (let [schema (m/schema (schema/malli-form (get schemas (keyword "properties" (namespace (:property/id property)))) schemas))]
        (when-not (m/validate schema property)
          (throw (ex-info (str (me/humanize (m/explain schema property)))
                          {:value property
                           :schema (m/form schema)})))))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))

(defn save!
  [{:keys [db/data db/file]}]
  (let [data (->> (vals data)
                  (sort-by #(keyword "properties" (namespace (:property/id %))))
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

(defn update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? data id))
  (let [schema (m/schema (schema/malli-form (get schemas (keyword "properties" (namespace (:property/id property)))) schemas))]
    (when-not (m/validate schema property)
      (throw (ex-info (str (me/humanize (m/explain schema property)))
                      {:value property
                       :schema (m/form schema)}))))
  (let [new-db (update this :db/data assoc id property)]
    (save! new-db)
    new-db))

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
       (filter #(= property-type (keyword "properties" (namespace (:property/id %)))))))

(declare build-values)

(defn- create-value [[k] v db]
  (case k
    :s/map
    (build-values (:db/schemas db) v db)

    :s/one-to-many
    (set (map (fn [property-id]
                (build-values (:db/schemas db)
                              (get-raw db property-id)
                              db))
              v))

    :s/one-to-one
    (build-values (:db/schemas db)
                  (get-raw db v)
                  db)

    v))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(defn build [{:keys [db/schemas] :as this} property-id]
  (build-values schemas
                (get-raw this property-id)
                this))

(defn build-all [{:keys [db/schemas] :as this} property-type]
  (map #(build-values schemas % this)
       (all-raw this property-type)))
