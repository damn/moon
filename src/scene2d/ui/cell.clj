(ns scene2d.ui.cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn set-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (cell/fill-x! cell)
      :fill-y?    (cell/fill-y! cell)
      :expand?    (cell/expand! cell)
      :expand-x?  (cell/expand-x! cell)
      :expand-y?  (cell/expand-y! cell)
      :bottom?    (cell/bottom! cell)
      :colspan    (cell/colspan! cell arg)
      :pad        (cell/pad! cell arg)
      :pad-top    (cell/pad-top! cell arg)
      :pad-bottom (cell/pad-bottom! cell arg)
      :width      (cell/width! cell arg)
      :height     (cell/height! cell arg)
      :center?    (cell/center! cell)
      :right?     (cell/right! cell)
      :left?      (cell/left! cell))))
