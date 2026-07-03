(ns scene2d.ui.table.set-opts
  (:require [clojure.gdx.layout.pack :as pack]
            [clojure.gdx.table.defaults :as defaults]
            [scene2d.ui.cell :as cell]
            [scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack/f table))
  (when-let [defaults-opts (:table/cell-defaults opts)]
    (cell/set-opts! (defaults/f table) defaults-opts)))
