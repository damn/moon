(ns moon.db.build
  (:require [moon.schemas.build-values :refer [build-values]]
            [moon.db.get-raw :refer [get-raw]]))

(defn build [{:keys [db/schemas] :as this} property-id]
  (build-values schemas
                (get-raw this property-id)
                this))
