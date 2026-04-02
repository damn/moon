(ns moon.table
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

; TODO order is important, reduce?
(defn- set-cell-opts! [^Cell cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (.fillX     cell)
      :fill-y?    (.fillY     cell)
      :expand?    (.expand    cell)
      :expand-x?  (.expandX   cell)
      :expand-y?  (.expandY   cell)
      :bottom?    (.bottom    cell)
      :colspan    (.colspan   cell (int   arg))
      :pad        (.pad       cell (float arg))
      :pad-top    (.padTop    cell (float arg))
      :pad-bottom (.padBottom cell (float arg))
      :width      (.width     cell (float arg))
      :height     (.height    cell (float arg))
      :center?    (.center    cell)
      :right?     (.right     cell)
      :left?      (.left      cell))))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
       (map? props-or-actor)
       ;(cell-fn table)
       (-> (table/add! table (:actor props-or-actor))
           (set-cell-opts! (dissoc props-or-actor :actor)))
       ; TODO Remove else case
       :else (table/add! table props-or-actor)))
    (table/row! table))
  table)

(defn set-cell-defaults! [table cell-opts]
  (set-cell-opts! (table/defaults table) cell-opts)
  table)
