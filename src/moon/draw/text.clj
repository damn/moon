(ns moon.draw.text
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)
           (com.badlogic.gdx.utils Align)))

(defn- draw-text! [^BitmapFont font batch {:keys [scale text x y up? h-align target-width wrap?]}]
  (let [text-height (fn []
                      (-> text
                          (str/split #"\n")
                          count
                          (* (.getLineHeight font))))
        old-scale (.scaleX (.getData font))]
    (.setScale (.getData font) (* old-scale scale))
    (.draw font
           batch
           text
           (float x)
           (float (+ y (if up? (text-height) 0)))
           (float target-width)
           (or h-align Align/center)
           wrap?)
    (.setScale (.getData font) old-scale)))

(defn do!
  [{:keys [graphics/batch
           graphics/unit-scale
           graphics/default-font]}
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
