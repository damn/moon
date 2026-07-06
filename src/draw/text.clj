(ns draw.text
  (:require
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font] [com.badlogic.gdx.utils.align :as align]
            [clojure.gdx.bitmap-font$bitmap-font-data.get-scale-x :as get-scale-x]
            [clojure.gdx.bitmap-font$bitmap-font-data.set-scale :as set-scale]
            [clojure.string :as str]))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        unit-scale @unit-scale
        scale (or scale 1)
        font-data (bitmap-font/get-data font)
        old-scale (get-scale-x/f font-data)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (set-scale/f font-data (* old-scale scale))
    (bitmap-font/draw! font
                  batch
                  text
                  x
                  (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (bitmap-font/get-line-height font)))
                         0))
                  target-width
                  align/center
                  wrap?)
    (set-scale/f font-data old-scale)))
