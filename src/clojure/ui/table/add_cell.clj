(ns clojure.ui.table.add-cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.cell :as cell]))

(defn add-cell! [table cell-declaration]
  (-> (table/add table (:actor cell-declaration))
      (cell/set-opts! (dissoc cell-declaration :actor))))
