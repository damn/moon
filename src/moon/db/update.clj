(ns moon.db.update
  (:require [moon.schemas.validate :refer [validate]]
            [moon.property.type :refer [property->type]]
            [moon.db.save :refer [save!]]))

(defn update! [{:keys [db/data db/schemas] :as this} {:keys [property/id] :as property}]
  (assert (contains? property :property/id))
  (assert (contains? data id))
  (validate schemas (property->type property) property)
  (let [new-db (update this :db/data assoc id property)]
    (save! new-db)
    new-db) )
