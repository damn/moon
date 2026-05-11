(ns moon.ui.impl.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [moon.ui.actor :as actor]
            [moon.ui.table :as table]
            [moon.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(extend-type Table
  table/Table
  (add! [table cell-declaration]
    (-> (.add table ^Actor (:actor cell-declaration))
        (cell/set-opts! (dissoc cell-declaration :actor))))

  (add-rows! [table rows]
    (doseq [row rows]
      (doseq [props-or-actor row]

        (cond
         (map? props-or-actor)
         (table/add! table props-or-actor)

         ; TODO Remove else case
         :else (.add table ^Actor props-or-actor)
         ))
      (.row table))
    table)

   (set-opts! [table opts]
     (when-let [rows (:table/rows opts)]
       (table/add-rows! table rows)
       (widget-group/pack! table))
     (when-let [defaults (:table/cell-defaults opts)]
       (cell/set-opts! (.defaults table) defaults))
     (widget-group/set-opts! table opts)))

(defmethod actor/create :ui/table [opts]
  (doto (Table.)
    (table/set-opts! opts)))
