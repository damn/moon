(ns moon.schemas
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.schema :as schema]
            [moon.utils :as utils]))

(defn build-values [schemas property db]
  (utils/apply-kvs property
                   (fn [k v]
                     (try (schema/create-value (get schemas k) v db)
                          (catch Throwable t
                            (throw (ex-info " " {:k k :v v} t)))))))

(defn default-value [schemas k]
  (let [schema (get schemas k)]
    (cond
     (#{:s/map} (schema 0)) {}
     :else nil)))

(defn validate [schemas k value]
  (-> (get schemas k)
      (schema/malli-form schemas)
      m/schema
      (mu/validate-humanize value)))

(defn create-map-schema [schemas ks]
  (mu/create-map-schema ks (fn [k]
                             (schema/malli-form (get schemas k) schemas))))
