(ns gdx.scenes.scene2d.ui.table
  (:require [scene2d.ui.table :as table]
            [scene2d.ui.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
