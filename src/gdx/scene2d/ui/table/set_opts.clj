(ns gdx.scene2d.ui.table.set-opts
  (:require [clojure.table :as table]
            [clojure.layout :as layout]
            [clojure.set-opts :as set-opts]
            [gdx.scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (layout/pack! table))
  (when-let [defaults-opts (:table/cell-defaults opts)]
    (set-opts/f (table/defaults table) defaults-opts)))
