(ns gdx.scene2d.ui.table
  (:require [clojure.table :as table]
            [gdx.scene2d.ui.table.set-opts :refer [set-opts!]]))

(defn create
  ([]
   (table/new))
  ([opts]
   (doto (create)
     (set-opts! opts))))
