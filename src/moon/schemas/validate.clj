(ns moon.schemas.validate
  (:require [clojure.malli-form :refer [malli-form]]
            [malli.create-schema :refer [create-schema]]
            [malli.utils.validate-humanize :refer [validate-humanize]]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      create-schema
      (validate-humanize value)))
