(ns gdx.scenes.scene2d.ui.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
