(ns draw.text
  (:require [clojure.string :as str]
            [bitmap-font-data.set-scale :as set-scale]
            [bitmap-font-data.scale-x :as scale-x])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)
           (com.badlogic.gdx.utils Align)))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        unit-scale @unit-scale
        scale (or scale 1)
        old-scale (scale-x/f (.getData ^BitmapFont font))
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (set-scale/f (.getData ^BitmapFont font) (* old-scale scale))
    (.draw ^BitmapFont font
           batch
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight ^BitmapFont font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (set-scale/f (.getData ^BitmapFont font) old-scale)))
