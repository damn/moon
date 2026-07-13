(ns gdx.actor.group.widget.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [gdx.cell :as cell]
            [gdx.layout :as layout]))

(defn add-cell! [table cell-declaration]
  (-> (table/add table (:actor cell-declaration))
      (cell/set-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (table/add table props-or-actor)))
    (table/row table))
  table)

(def ^:private set-opt-fns
  {:table/rows (fn [table rows]
                 (add-rows! table rows)
                 (layout/pack table))
   :table/cell-defaults (fn [table defaults]
                          (cell/set-opts! (table/defaults table) defaults))})

(defn set-opts! [table opts]
  (doseq [[k v] opts :when (set-opt-fns k)]
    ((set-opt-fns k) table v))
  table)

(defn create [opts]
  (doto (table/new)
    (set-opts! opts)))
