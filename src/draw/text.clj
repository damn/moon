(ns draw.text
  (:require [clojure.gdx :as gdx]
            [clojure.string :as str]))

(defn f
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        unit-scale @unit-scale
        scale (or scale 1)
        old-scale (gdx/font-get-scale-x font)
        target-width 0
        wrap? false
        scale (* (float unit-scale)
                 (float scale))]
    (gdx/font-set-scale! font (* old-scale scale))
    (gdx/font-draw! font
                    batch
                    text
                    (float x)
                    (float (+ y (if up?
                                  (-> text
                                      (str/split #"\n")
                                      count
                                      (* (gdx/font-get-line-height font)))
                                  0)))
                    (float target-width)
                    gdx/align-center
                    wrap?)
    (gdx/font-set-scale! font old-scale)))
