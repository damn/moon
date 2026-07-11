(ns clojure.schemas-validate
  (:require [moon.malli :as schema]
            [moon.schema :as moon-schema]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (moon-schema/malli-form schemas)
      schema/create
      (schema/validate-humanize value)))
