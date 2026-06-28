(ns scene2d.ui.table.set-opts
  (:require [scene2d.ui.cell :as cell]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.utils.layout.pack :refer [pack!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [^Table table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (.defaults table) defaults)))
