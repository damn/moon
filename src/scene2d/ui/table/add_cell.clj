(ns scene2d.ui.table.add-cell
  (:require [scene2d.ui.cell :refer [set-opts!]]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defn add-cell! [table cell-declaration]
  (-> (table/add table (:actor cell-declaration))
      (set-opts! (dissoc cell-declaration :actor))))
