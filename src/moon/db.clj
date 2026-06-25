(ns moon.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.schemas.validate :refer [validate]]
            [moon.property.type :refer [property->type]]))

(defn create []
  (let [schemas (-> "config/schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "config/properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (validate schemas (property->type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))
