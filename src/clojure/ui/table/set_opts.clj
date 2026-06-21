(ns clojure.ui.table.set-opts
  (:require [clojure.ui.cell :as cell]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.utils.layout.pack :refer [pack!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (Table/.defaults table) defaults)))
