(ns clojure.schemas-validate
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.create-schema :refer [create-schema]]
            [clojure.validate-humanize :refer [validate-humanize]]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      create-schema
      (validate-humanize value)))
