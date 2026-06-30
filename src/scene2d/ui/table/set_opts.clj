(ns scene2d.ui.table.set-opts
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (gdx/pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (gdx/set-opts! (gdx/table-defaults table) defaults)))
