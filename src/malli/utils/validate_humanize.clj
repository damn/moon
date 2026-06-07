(ns malli.utils.validate-humanize
  (:require [malli.create-ex-info :refer [create-ex-info]]
            [malli.validate :refer [validate]]))

(defn validate-humanize [schema value]
  (when-not (validate schema value)
    (throw (create-ex-info schema value))))
