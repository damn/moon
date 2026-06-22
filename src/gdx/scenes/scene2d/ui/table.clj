(ns gdx.scenes.scene2d.ui.table
  (:require [gdl.ui.table :as table]
            [gdl.ui.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
