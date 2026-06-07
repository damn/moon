(ns malli.create-ex-info
  (:require [malli.core :as m]
            [malli.error :as me]))

(defn create-ex-info [schema value]
  (ex-info (str (me/humanize (m/explain schema value)))
           {:value value
            :schema (m/form schema)}))
