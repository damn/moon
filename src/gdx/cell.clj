(ns gdx.cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn set-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (cell/fillX cell)
      :fill-y?    (cell/fillY cell)
      :expand?    (cell/expand cell)
      :expand-x?  (cell/expandX cell)
      :expand-y?  (cell/expandY cell)
      :bottom?    (cell/bottom cell)
      :colspan    (cell/colspan cell arg)
      :pad        (cell/pad cell arg)
      :pad-top    (cell/padTop cell arg)
      :pad-bottom (cell/padBottom cell arg)
      :width      (cell/width cell arg)
      :height     (cell/height cell arg)
      :center?    (cell/center cell)
      :right?     (cell/right cell)
      :left?      (cell/left cell))))
