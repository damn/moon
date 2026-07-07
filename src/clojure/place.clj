(ns clojure.place
  (:require [clojure.posis :as posis]
            [clojure.place-step :as place-step]))

(defn step
  [{:keys [scale
           scaled-grid
           grid
           schema-tiled-map]
    :as w}]
  (assoc w :scaled-grid (place-step/f schema-tiled-map
                                      scale
                                      scaled-grid
                                      grid
                                      (filter #(= :ground     (get grid %)) (posis/f grid))
                                      (filter #(= :transition (get grid %)) (posis/f grid)))))
