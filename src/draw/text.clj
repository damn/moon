(ns draw.text
  (:require [clojure.string :as str]
            [clojure.bitmap-font.draw :as draw]
            [clojure.bitmap-font.get-scale-x :as get-scale-x]
            [clojure.bitmap-font.get-line-height :as get-line-height]
            [clojure.bitmap-font.set-scale :as set-scale]
            [clojure.utils.align :as align]))

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
