(ns draw.text
  (:require [clojure.string :as str]
            [clojure.gdx.graphics.g2d.bitmap-font :as font]
            [clojure.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [clojure.gdx.utils.align :as align]))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text h-align up?]}]
  (let [font (or font default-font)
        old-scale (font.data/scale-x (font/data font))
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float (or scale 1)))]
    (font.data/set-scale! (font/data font) (* old-scale scale))
    (font/draw! font
                batch
                text
                x
                (+ y (if up?
                       (-> text
                           (str/split #"\n")
                           count
                           (* (font/line-height font)))
                       0))
                target-width
                (align/k->value :align/center)
                wrap?)
    (font.data/set-scale! (font/data font) old-scale)))
