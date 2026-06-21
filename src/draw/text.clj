(ns draw.text
  (:require [clojure.string :as str]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.draw :as draw]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.get-scale-x :as get-scale-x]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.get-line-height :as get-line-height]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.set-scale :as set-scale]
            [com.badlogic.gdx.utils.align :as align]))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        unit-scale @unit-scale
        scale (or scale 1)
        old-scale (get-scale-x/f font)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (set-scale/f! font (* old-scale scale))
    (draw/f! font
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
             align/center
             wrap?)
    (set-scale/f! font old-scale)))
