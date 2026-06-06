(ns draw.text
  (:require [gdx.draw-text :refer [draw-text!]]))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (draw-text! (or font default-font)
              batch
              @unit-scale
              (or scale 1)
              text
              x
              y
              up?))
