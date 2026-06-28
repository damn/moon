(ns draw.text
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)
           (com.badlogic.gdx.utils Align)))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (let [^BitmapFont font (or font default-font)
        unit-scale @unit-scale
        scale (or scale 1)
        old-scale (.scaleX (.getData font))
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (.setScale (.getData font) (* old-scale scale))
    (.draw font
           batch
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (.setScale (.getData font) old-scale)))
