(ns clojure.moon.render.stats
  (:require [clojure.stats.get-hitpoints :as get-hitpoints]
            [clojure.val-max.ratio :as ratio]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]))

(defn f
  [_ entity {:keys [ctx/colors]}]
  (let [ratio (ratio/f (get-hitpoints/f (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [body/position body/width body/height]} (:entity/body entity)
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 world-unit-scale)
            border (* 1 world-unit-scale)]
        [[:draw/filled-rectangle x y width height (:colors/hp-bar-rect colors)]
         [:draw/filled-rectangle
          (+ x border)
          (+ y border)
          (- (* width ratio) (* 2 border))
          (- height          (* 2 border))
          ((:colors/hp-bar colors) ratio)]]))))
