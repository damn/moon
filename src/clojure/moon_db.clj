(ns clojure.moon-db
  (:require [clojure.edn :as edn]
            [clojure.edn-resource :refer [edn-resource]]
            [clojure.java.io :as io]
            [clojure.schemas-validate :refer [validate]]
            [clojure.type :refer [property->type]]))

(defn create []
  (let [schemas (-> (-> "config/schema.edn" io/resource slurp edn/read-string)
                    (with-meta {:schemas/k->malli-form (edn-resource "config/k->malli-form.edn")}))
        properties-file (io/resource "config/properties.edn")
        properties (-> properties-file slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (validate schemas (property->type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/file properties-file
     :db/schemas schemas}))
