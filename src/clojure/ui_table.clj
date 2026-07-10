(ns clojure.ui-table
  (:require [gdl.scenes.scene2d.ui.table :as table]
            [clojure.table-set-opts :refer [set-opts!]]))

(defn create
  ([]
   (table/new))
  ([opts]
   (doto (create)
     (set-opts! opts))))
