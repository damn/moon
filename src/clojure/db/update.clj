(ns clojure.db.update
  (:require [clojure.db.save :refer [save!]]
            [clojure.schemas-validate :refer [validate]]
            [clojure.type :refer [property->type]]))

(defn update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? data id))
  (validate schemas (property->type property) property)
  (let [new-db (update this :db/data assoc id property)]
    (save! new-db)
    new-db))
