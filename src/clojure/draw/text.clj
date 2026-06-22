(ns clojure.draw.text
  (:require [clojure.string :as str]
            [clojure.bitmap-font.draw :as draw]
            [clojure.bitmap-font.get-line-height :as get-line-height]
            [clojure.bitmap-font.get-data :refer [get-data]]
            [clojure.bitmap-font-data.set-scale :as set-scale]
            [clojure.bitmap-font-data.scale-x :as scale-x]
            [clojure.align :as align]))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        unit-scale @unit-scale
        scale (or scale 1)
        old-scale (scale-x/f (get-data font))
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (set-scale/f (get-data font) (* old-scale scale))
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
    (set-scale/f (get-data font) old-scale)))
