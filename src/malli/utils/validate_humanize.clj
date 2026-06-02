(ns malli.utils.validate-humanize
  (:require [malli.core :as m]
            [malli.error :as me]))

(defn validate-humanize [schema value]
  (when-not (m/validate schema value)
    (throw (ex-info (str (me/humanize (m/explain schema value)))
                    {:value value
                     :schema (m/form schema)}))))
