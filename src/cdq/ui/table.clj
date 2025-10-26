(ns cdq.ui.table
  (:require [cdq.ui.widget-group :as widget-group]
            [moon.scene2d.ui.cell :as cell]
            [moon.scene2d.ui.table :as table]
            [moon.ui.table :as vis-table]))

(defn add! [table actor]
  (table/add! table actor))

(defn cells [table]
  (table/cells table))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
       (map? props-or-actor) (-> (add! table (:actor props-or-actor))
                                 (cell/set-opts! (dissoc props-or-actor :actor)))
       :else (add! table props-or-actor)))
    (table/row! table))
  table)

(defn set-opts! [table {:keys [rows cell-defaults] :as opts}]
  (cell/set-opts! (.defaults table) cell-defaults)
  (doto table
    (add-rows! rows)
    (widget-group/set-opts! opts)))

(defn create [opts]
  (-> (vis-table/create)
      (set-opts! opts)))
