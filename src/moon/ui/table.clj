(ns moon.ui.table
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [moon.table :as x-table]))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (x-table/add-rows! table rows)
    (widget-group/pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (x-table/set-cell-defaults! table defaults)))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
