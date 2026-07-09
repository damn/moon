(ns clojure.validate-humanize
  (:require [clojure.malli.schema :as malli-schema]
            [clojure.malli-validate :refer [validate]]))

(defn validate-humanize [schema value]
  (when-not (validate schema value)
    (throw (malli-schema/create-ex-info schema value))))
