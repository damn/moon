(ns scene2d.ui.table.set-opts
  (:require [clojure.gdx.layout.pack :as pack]
            [scene2d.ui.cell :as cell]
            [scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [^Table table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack/f table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (.defaults table) defaults)))
