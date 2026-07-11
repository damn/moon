(ns clojure.schemas-validate
  (:require [clojure.malli-form :refer [malli-form]]
            [moon.malli :as schema]))

(defn validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      schema/create
      (schema/validate-humanize value)))
