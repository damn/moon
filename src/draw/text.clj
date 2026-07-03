(ns draw.text
  (:require [clojure.gdx.align.center :as align-center]
            [clojure.gdx.bitmap-font.draw! :as draw-font!]
            [clojure.gdx.bitmap-font.get-data :as get-data]
            [clojure.gdx.bitmap-font.get-line-height :as get-line-height]
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
        font-data (get-data/f font)
        old-scale (get-scale-x/f font-data)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (set-scale/f font-data (* old-scale scale))
    (draw-font!/f font
                  batch
                  text
                  x
                  (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (get-line-height/f font)))
                         0))
                  target-width
                  align-center/v
                  wrap?)
    (set-scale/f font-data old-scale)))
