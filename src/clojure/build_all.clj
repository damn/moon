(ns clojure.build-all
  (:require [clojure.all-raw :refer [all-raw]]
            [clojure.create-value :refer [build-values]]))

(defn build-all [{:keys [db/schemas] :as this} property-type]
  (map #(build-values schemas % this)
       (all-raw this property-type)))
