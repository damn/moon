(ns clojure.create-value
  (:require [clojure.db.get-raw :refer [get-raw]]))

(defmulti create-value (fn [[k] _v _db]
                         k))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(defmethod create-value :default
  [_ v _db]
  v)

(defmethod create-value :s/map
  [_ v db]
  (build-values (:db/schemas db) v db))

(defmethod create-value :s/one-to-many
  [_ property-ids db]
  (set (map (fn [property-id]
              (build-values (:db/schemas db)
                            (get-raw db property-id)
                            db))
            property-ids)))

(defmethod create-value :s/one-to-one
  [_ property-id db]
  (build-values (:db/schemas db)
                (get-raw db property-id)
                db))


