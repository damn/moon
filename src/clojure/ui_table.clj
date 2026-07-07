(ns clojure.ui-table
  (:require [clojure.table :as table]
            [clojure.table-set-opts :refer [set-opts!]]))

(defn create
  ([]
   (table/new))
  ([opts]
   (doto (create)
     (set-opts! opts))))
