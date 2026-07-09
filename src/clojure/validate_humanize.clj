(ns clojure.validate-humanize
  (:require [clojure.malli.schema :as malli-schema]))

(defn validate-humanize [schema value]
  (when-not (malli-schema/validate schema value)
    (throw (malli-schema/create-ex-info schema value))))
