(ns clojure.table-set-opts
  (:require [clojure.table :as table]
            [clojure.pack! :as pack!]
            [clojure.ui.cell.set-opts :as set-opts]
            [clojure.ui.table.add-rows :refer [add-rows!]]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack!/f table))
  (when-let [defaults-opts (:table/cell-defaults opts)]
    (set-opts/f (table/defaults table) defaults-opts)))
