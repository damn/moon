(ns moon.db.build-all
  (:require [moon.schemas.build-values :refer [build-values]]
            [moon.db.all-raw :refer [all-raw]]))

(defn build-all [{:keys [db/schemas] :as this} property-type]
  (map #(build-values schemas % this)
       (all-raw this property-type)))
