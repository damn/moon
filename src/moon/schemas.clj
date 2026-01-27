(ns moon.schemas
  (:require [malli.core :as m]
            [malli.utils :as mu]
            [moon.schema :as schema]))

; create-value
; malli-form
; only used here (map widget doesnt need to know)
; => so only malli here !?
; => defmethods together !?... wtf

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (schema/create-value (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

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
