(ns clojure.schemas-validate
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli.schema :as schema]
            [clojure.validate-humanize :refer [validate-humanize]]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      schema/create
      (validate-humanize value)))
