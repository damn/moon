(ns moon.impl.db
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.property :as property]
            [moon.schemas :as schemas]))

(defn create [_ctx]
  (let [schemas (-> "schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (schemas/validate schemas (property/type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))
