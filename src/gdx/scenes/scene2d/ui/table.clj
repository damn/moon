(ns gdx.scenes.scene2d.ui.table
  (:require [ui.table :as table]
            [ui.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
