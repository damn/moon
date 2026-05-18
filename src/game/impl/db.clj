(ns game.impl.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [moon.db :as db]
            [moon.property :as property]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.map :as map]
            [moon.schemas :as schemas]
            [moon.val-max :as val-max]))

(defmulti create-value (fn [[k] _v _db]
                         k))

(defmethod create-value :default [_ v _db]
  v)

(defmulti malli-form (fn [[k] _schemas]
                       k))

(defrecord Schemas []
  schemas/Schemas
  (create-map-schema [schemas ks]
    (mu/create-map-schema ks (fn [k]
                               (malli-form (get schemas k) schemas))))

  (validate [schemas k value]
    (-> (get schemas k)
        (malli-form schemas)
        m/schema
        (mu/validate-humanize value)))

  (build-values [schemas property db]
    (reduce (fn [m k]
              (assoc m k
                     (try (create-value (get schemas k) (k m) db)
                          (catch Throwable t
                            (throw (ex-info " " {:k k
                                                 :v (k m)} t))))))
            property
            (keys property)))

  (default-value [schemas k]
    (let [schema (get schemas k)]
      (cond
       (#{:s/map} (schema 0)) {}
       :else nil)))

  (optional-keyset [schemas schema]
    (mu/optional-keyset (malli-form schema schemas)))

  (optional? [schemas schema k]
    (mu/optional? k (malli-form schema schemas)))

  (map-keys [schemas schema]
    (mu/map-keys (malli-form schema schemas))))

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

(defrecord DB []
  db/DB
  (property-types [{:keys [db/schemas]}]
    (filter #(= "properties" (namespace %)) (keys schemas)))

  (update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
    (assert (contains? property :property/id))
    (assert (contains? data id))
    (schemas/validate schemas (property/type property) property)
    (let [new-db (update this :db/data assoc id property)]
      (save! new-db)
      new-db) )

  (delete! [{:keys [db/data] :as this} property-id]
    (assert (contains? data property-id))
    (let [new-db (update this :db/data dissoc property-id)]
      (save! new-db)
      new-db))

  (get-raw [{:keys [db/data]} property-id]
    (assert (contains? data property-id))
    (get data property-id) )

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

(defn create [_ctx]
  (let [schemas (map->Schemas (-> "schema.edn" io/resource slurp edn/read-string))
        properties-file (io/resource "properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (schemas/validate schemas (property/type property) property))
    (map->DB
     {:db/data (zipmap (map :property/id properties) properties)
      :db/file properties-file
      :db/schemas schemas})))

(defmethod create-value :s/map [_ v db]
  (schemas/build-values (:db/schemas db) v db))

(defmethod create-value :s/one-to-many [_ property-ids db]
  (set (map (partial db/build db) property-ids)))

(defmethod create-value :s/one-to-one [_ property-id db]
  (db/build db property-id))

(defmethod malli-form :s/animation [_ schemas]
  (schemas/create-map-schema schemas
                             [:animation/frames
                              :animation/frame-duration
                              :animation/looping?]))

(defmethod malli-form :s/boolean [_ _schemas]
  :boolean)

(defmethod malli-form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))

(defmethod malli-form :s/image [_ schemas]
  (schemas/create-map-schema schemas
                             [:image/file
                              [:image/bounds {:optional true}]]))

(defmethod malli-form :s/map [[_ ks] schemas]
  (schemas/create-map-schema schemas ks))

(defmethod malli-form :s/number [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defmethod malli-form :s/one-to-many [[_ property-type] _schemas]
  [:set [:qualified-keyword {:namespace (property/type->id-namespace property-type)}]])

(defmethod malli-form :s/one-to-one [[_ property-type] _schemas]
  [:qualified-keyword {:namespace (property/type->id-namespace property-type)}])

(defmethod malli-form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))

(defmethod malli-form :s/some [_ _schemas]
  :some)

(defmethod malli-form :s/sound [_ _schemas]
  :string)

(defmethod malli-form :s/string [_ _schemas]
  :string)

(defmethod malli-form :s/val-max [_ _schemas]
  val-max/schema)

(defmethod malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
