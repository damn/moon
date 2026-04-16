(ns clojure.gdx.ui.table
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [gdl.scene2d.ui.widget-group :as widget-group]))

; TODO order is important, reduce?
(defn- set-cell-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (cell/fill-x!     cell)
      :fill-y?    (cell/fill-y!     cell)
      :expand?    (cell/expand!     cell)
      :expand-x?  (cell/expand-x!   cell)
      :expand-y?  (cell/expand-y!   cell)
      :bottom?    (cell/bottom!     cell)
      :colspan    (cell/colspan!    cell arg)
      :pad        (cell/pad!        cell arg)
      :pad-top    (cell/pad-top!    cell arg)
      :pad-bottom (cell/pad-bottom! cell arg)
      :width      (cell/width!      cell arg)
      :height     (cell/height!     cell arg)
      :center?    (cell/center!     cell)
      :right?     (cell/right!      cell)
      :left?      (cell/left!       cell))))

(defn add! [table cell-declaration]
  (-> (table/add! table (:actor cell-declaration))
      (set-cell-opts! (dissoc cell-declaration :actor))))

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

(defn set-cell-defaults! [table cell-opts]
  (set-cell-opts! (table/defaults table) cell-opts)
  table)

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (widget-group/pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (set-cell-defaults! table defaults))
  (widget-group/set-opts! table opts))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
