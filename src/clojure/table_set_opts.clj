(ns clojure.table-set-opts
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clojure.pack! :as pack!]
            [clojure.ui.cell.set-opts :as set-opts]
            [clojure.ui.table.add-rows :refer [add-rows!]]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack!/f table))
  (when-let [defaults-opts (:table/cell-defaults opts)]
    (set-opts/f (table/defaults table) defaults-opts)))
