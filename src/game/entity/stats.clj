(ns game.entity.stats
  (:require [game.ctx :as ctx]
            [moon.entity :as entity]
            [moon.stats :as stats]
            [moon.val-max :as val-max]))

(defmethod entity/create :entity/stats
  [[_ v] _ctx]
  (stats/create v))

(defmethod entity/render :entity/stats
  [_ entity {:keys [ctx/colors] :as ctx}]
  (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [body/position body/width body/height]} (:entity/body entity)
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 (ctx/world-unit-scale ctx))
            border (* 1 (ctx/world-unit-scale ctx))]
        [[:draw/filled-rectangle x y width height (:colors/hp-bar-rect colors)]
         [:draw/filled-rectangle
          (+ x border)
          (+ y border)
          (- (* width ratio) (* 2 border))
          (- height          (* 2 border))
          ((:colors/hp-bar colors) ratio)]]))))
