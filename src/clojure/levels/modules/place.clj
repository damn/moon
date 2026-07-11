(ns clojure.levels.modules.place
  (:require [moon.g2d :as g2d]
            [clojure.levels.modules.place-step :as place-step]))

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
                                      (filter #(= :ground     (get grid %)) (g2d/posis grid))
                                      (filter #(= :transition (get grid %)) (g2d/posis grid)))))
