(ns moon.schemas
  (:require [malli.core :as m]
            [malli.utils :as mu]
            [moon.schema :as schema]
            [moon.utils :as utils]))

; create-value
; malli-form
; only used here (map widget doesnt need to know)
; apply-kvs only used here
; => so only malli here !?
; => defmethods together !?... wtf

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
