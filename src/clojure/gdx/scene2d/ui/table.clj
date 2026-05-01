(ns clojure.gdx.scene2d.ui.table
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Cell
                                               Table)))

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
      :colspan    (.colspan   cell (int arg))
      :pad        (.pad       cell (float arg))
      :pad-top    (.padTop    cell (float arg))
      :pad-bottom (.padBottom cell (float arg))
      :width      (.width     cell (float arg))
      :height     (.height    cell (float arg))
      :center?    (.center    cell)
      :right?     (.right     cell)
      :left?      (.left      cell))))

(defn add! [^Table table cell-declaration]
  (-> (.add table ^Actor (:actor cell-declaration))
      (set-cell-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [^Table table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]

      (cond
       (map? props-or-actor)
       (add! table props-or-actor)

       ; TODO Remove else case
       :else (.add table ^Actor props-or-actor)

       ))
    (.row table))
  table)

(defn set-cell-defaults! [^Table table cell-opts]
  (set-cell-opts! (.defaults table) cell-opts)
  table)

(defn set-opts! [^Table table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (widget-group/pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (set-cell-defaults! table defaults))
  (widget-group/set-opts! table opts))

(defmethod actor/create :ui/table [opts]
  (doto (Table.)
    (set-opts! opts)))
