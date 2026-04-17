(ns moon.draw.text
  (:require [clojure.graphics.bitmap-font :as bitmap-font]))

(defn- draw-text! [font batch {:keys [scale text x y up? h-align target-width wrap?]}]
  (let [old-scale (bitmap-font/scale-x font)]
    (bitmap-font/set-scale! font (* old-scale scale))
    (bitmap-font/draw! font
                       batch
                       text
                       x
                       (+ y (if up? (bitmap-font/text-height font text) 0))
                       target-width
                       (or h-align :align/center)
                       wrap?)
    (bitmap-font/set-scale! font old-scale)))

(defn do!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text h-align up?]}]
  (draw-text! (or font default-font)
              batch
              {:scale (* (float @unit-scale)
                         (float (or scale 1)))
               :text text
               :x x
               :y y
               :up? up?
               :h-align h-align
               :target-width 0
               :wrap? false}))
