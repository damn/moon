(ns scene2d.ui.table.set-opts
  (:require [scene2d.ui.cell :as cell]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.utils.layout.pack :refer [pack!]]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (table/defaults table) defaults)))
