(ns gdl.ui.table.set-opts
  (:require [gdl.ui.cell :as cell]
            [gdl.ui.table.add-rows :refer [add-rows!]]
            [gdl.layout.pack :refer [pack!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (Table/.defaults table) defaults)))
