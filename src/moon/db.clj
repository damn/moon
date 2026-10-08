(ns moon.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [malli.core :as m]
            [malli.error :as me]
            [moon.m :refer [recur-sort]]
            [moon.schema :as schema]))

(defn create [schemas]
  (let [properties-file (io/resource "properties.edn")
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
     :db/file properties-file}))

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

(defn update! [db schemas {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? (:db/data db) id))
  (let [schema (m/schema (schema/malli-form (get schemas (keyword "properties" (namespace (:property/id property)))) schemas))]
    (when-not (m/validate schema property)
      (throw (ex-info (str (me/humanize (m/explain schema property)))
                      {:value property
                       :schema (m/form schema)}))))
  (let [new-db (update db :db/data assoc id property)]
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

(defn- create-value [[k] v schemas db]
  (case k
    :s/map
    (build-values schemas v db)

    :s/one-to-many
    (set (map (fn [property-id]
                (build-values schemas
                              (get-raw db property-id)
                              db))
              v))

    :s/one-to-one
    (build-values schemas
                  (get-raw db v)
                  db)

    v))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) schemas db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(defn build [db schemas property-id]
  (build-values schemas
                (get-raw db property-id)
                db))

(defn build-all [db schemas property-type]
  (map #(build-values schemas % db)
       (all-raw db property-type)))
