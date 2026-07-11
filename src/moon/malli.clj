(ns moon.malli
  (:require [malli.core :as m]
            [malli.error :as me]))

(defn create [schema-form]
  (m/schema schema-form))

(defn create-ex-info [schema value]
  (ex-info (str (me/humanize (m/explain schema value)))
           {:value value
            :schema (m/form schema)}))

(defn validate [schema value]
  (m/validate schema value))

(defn validate-humanize [schema value]
  (when-not (validate schema value)
    (throw (create-ex-info schema value))))
