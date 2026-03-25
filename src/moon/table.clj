(ns moon.table
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Cell
                                               Table)))

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

(defn add-rows! [^Table table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
       (map? props-or-actor)
       ;(cell-fn table)
       (-> (.add table ^Actor (:actor props-or-actor))
           (set-cell-opts! (dissoc props-or-actor :actor)))
       ; TODO Remove else case
       :else (.add table ^Actor props-or-actor)))
    (.row table))
  table)

(defn set-cell-defaults! [^Table table cell-opts]
  (set-cell-opts! (.defaults table) cell-opts)
  table)
