(ns scene2d.ui.table.add-cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.cell :refer [set-opts!]]))

(defn add-cell! [table cell-declaration]
  (-> (table/add! table (:actor cell-declaration))
      (set-opts! (dissoc cell-declaration :actor))))
