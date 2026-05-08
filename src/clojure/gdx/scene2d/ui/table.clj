(ns clojure.gdx.scene2d.ui.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.ui.actor :as actor]
            moon.ui.table
            [clojure.gdx.scene2d.ui.widget-group :as widget-group]))

(extend com.badlogic.gdx.scenes.scene2d.ui.Table
  moon.ui.table/Table
  {
   :add!
   (fn [table cell-declaration]
     (-> (table/add! table (:actor cell-declaration))
         (cell/set-opts! (dissoc cell-declaration :actor))))

   :add-rows!
   (fn [table rows]
     (doseq [row rows]
       (doseq [props-or-actor row]

         (cond
          (map? props-or-actor)
          (moon.ui.table/add! table props-or-actor)

          ; TODO Remove else case
          :else (table/add! table props-or-actor)

          ))
       (table/row! table))
     table)

   :set-opts!
   (fn [table opts]
     (when-let [rows (:table/rows opts)]
       (moon.ui.table/add-rows! table rows)
       (widget-group/pack! table))
     (when-let [defaults (:table/cell-defaults opts)]
       (cell/set-opts! (table/defaults table) defaults))
     (widget-group/set-opts! table opts))
   }
  )

(defmethod actor/create :ui/table [opts]
  (doto (table/create)
    (moon.ui.table/set-opts! opts)))
