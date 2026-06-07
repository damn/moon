(ns com.badlogic.gdx.scenes.scene2d.ui.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defprotocol SetOpts
  (set-opts! [_ opts]))

(extend-type Cell
  com.badlogic.gdx.scenes.scene2d.ui.cell/SetOpts
  (set-opts! [cell opts]
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
        :left?      (.left      cell)))))
