(ns clojure.schemas-validate
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli.schema :as schema]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      schema/create
      (schema/validate-humanize value)))
