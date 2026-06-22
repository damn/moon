(ns gdx.scenes.scene2d.ui.table
  (:require [gdl.table :as table]
            [gdl.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
