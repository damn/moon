(ns game.ui.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.ui.table]
            [gdl.scene2d.ui.widget-group :as widget-group]))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Table
  gdl.scene2d.ui.table/Table
  (add! [table cell-declaration]
    (-> (table/add! table (:actor cell-declaration))
        (cell/set-opts! (dissoc cell-declaration :actor))))

  (add-rows! [table rows]
    (doseq [row rows]
      (doseq [props-or-actor row]

        (cond
         (map? props-or-actor)
         (gdl.scene2d.ui.table/add! table props-or-actor)

         ; TODO Remove else case
         :else (table/add! table props-or-actor)
         ))
      (table/row! table))
    table)

   (set-opts! [table opts]
     (when-let [rows (:table/rows opts)]
       (gdl.scene2d.ui.table/add-rows! table rows)
       (widget-group/pack! table))
     (when-let [defaults (:table/cell-defaults opts)]
       (cell/set-opts! (table/defaults table) defaults))
     (widget-group/set-opts! table opts)))

(defmethod actor/create :ui/table [opts]
  (doto (table/create)
    (gdl.scene2d.ui.table/set-opts! opts)))
