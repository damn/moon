(ns clojure.malli.validate-humanize
  (:require [clojure.malli.create-ex-info :refer [create-ex-info]]
            [clojure.malli.validate :refer [validate]]))

(defn validate-humanize [schema value]
  (when-not (validate schema value)
    (throw (create-ex-info schema value))))
