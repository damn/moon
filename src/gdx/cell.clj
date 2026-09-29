(ns gdx.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn set-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (.fillX ^Cell cell)
      :fill-y?    (.fillY ^Cell cell)
      :expand?    (.expand ^Cell cell)
      :expand-x?  (.expandX ^Cell cell)
      :expand-y?  (.expandY ^Cell cell)
      :bottom?    (.bottom ^Cell cell)
      :colspan    (.colspan ^Cell cell (int arg))
      :pad        (.pad ^Cell cell (float arg))
      :pad-top    (.padTop ^Cell cell (float arg))
      :pad-bottom (.padBottom ^Cell cell (float arg))
      :width      (.width ^Cell cell (float arg))
      :height     (.height ^Cell cell (float arg))
      :center?    (.center ^Cell cell)
      :right?     (.right ^Cell cell)
      :left?      (.left ^Cell cell))))
