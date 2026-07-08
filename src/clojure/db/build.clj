(ns clojure.db.build
  (:require [clojure.create-value :refer [build-values]]
            [clojure.db.get-raw :refer [get-raw]]))

(defn build [{:keys [db/schemas] :as this} property-id]
  (build-values schemas
                (get-raw this property-id)
                this))
