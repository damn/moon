(ns gdx.scenes.scene2d.ui.table
  (:require [clojure.gdx.scene2d.ui.cell :as cell]
            [clojure.gdx.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.actor.create :as actor]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]))

(defn add! [table cell-declaration]
  (-> (table/add! table (:actor cell-declaration))
      (cell/set-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]

      (cond
       (map? props-or-actor)
       (add! table props-or-actor)

       ; TODO Remove else case
       :else (table/add! table props-or-actor)
       ))
    (table/row! table))
  table)

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (table/defaults table) defaults))
  (widget-group/set-opts! table opts))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
