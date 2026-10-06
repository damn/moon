(ns moon.schemas
  (:require [malli.core :as m]
            [malli.error :as me]
            [moon.schema :as schema]))

(defn validate [schemas k value]
  (let [schema (m/schema (schema/malli-form (get schemas k) schemas))]
    (when-not (m/validate schema value)
      (throw (ex-info (str (me/humanize (m/explain schema value)))
                      {:value value
                       :schema (m/form schema)})))))
