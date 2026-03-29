(ns moon.draw.text
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font.data :as bitmap-font.data]
            [clj.api.com.badlogic.gdx.utils.align :as align]
            [clojure.string :as str]))

(defn- draw-text! [font batch {:keys [scale text x y up? h-align target-width wrap?]}]
  (let [text-height (fn []
                      (-> text
                          (str/split #"\n")
                          count
                          (* (bitmap-font/line-height font))))
        old-scale (bitmap-font.data/scale-x (bitmap-font/data font))]
    (bitmap-font.data/set-scale! (bitmap-font/data font) (* old-scale scale))
    (bitmap-font/draw! font
                       batch
                       text
                       x
                       (+ y (if up? (text-height) 0))
                       target-width
                       (or h-align align/center)
                       wrap?)
    (bitmap-font.data/set-scale! (bitmap-font/data font) old-scale)))

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
