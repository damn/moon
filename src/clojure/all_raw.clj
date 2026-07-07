(ns clojure.all-raw
  (:require [clojure.type :refer [property->type]]))

(defn all-raw [{:keys [db/data]} property-type]
  (->> (vals data)
       (filter #(= property-type (property->type %)))))
