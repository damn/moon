(ns clojure.text
  (:require [clojure.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [clojure.bitmap-font :as bitmap-font]
            [clojure.align :as align]
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
        old-scale (bitmap-font-data/get-scale-x font-data)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (bitmap-font-data/set-scale! font-data (* old-scale scale))
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
    (bitmap-font-data/set-scale! font-data old-scale)))
