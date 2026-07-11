(ns moon.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.cell :as cell]))

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
