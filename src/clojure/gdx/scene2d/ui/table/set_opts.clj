(ns clojure.gdx.scene2d.ui.table.set-opts
  (:require [clojure.gdx.scene2d.ui.cell :as cell]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [clojure.gdx.scene2d.ui.widget-group.set-opts :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (Table/.defaults table) defaults))
  (widget-group/set-opts! table opts))
