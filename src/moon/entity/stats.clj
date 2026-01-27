(ns moon.entity.stats
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [moon.color :as colors]
            [moon.stats :as stats]
            [moon.val-max :as val-max]))

(defn create
  [[_ v] _ctx]
  (-> v
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))

(def ^:private hpbar-colors
  {:green     (color/float-bits [0 0.8 0 1])
   :darkgreen (color/float-bits [0 0.5 0 1])
   :yellow    (color/float-bits [0.5 0.5 0 1])
   :red       (color/float-bits [0.5 0 0 1])})

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color hpbar-colors)))

(def ^:private borders-px 1)

(defn- draw-hpbar [world-unit-scale {:keys [body/position body/width body/height]} ratio]
  (let [[x y] position]
    (let [x (- x (/ width  2))
          y (+ y (/ height 2))
          height (* 5          world-unit-scale)
          border (* borders-px world-unit-scale)]
      [[:draw/filled-rectangle x y width height (color/float-bits colors/black)]
       [:draw/filled-rectangle
        (+ x border)
        (+ y border)
        (- (* width ratio) (* 2 border))
        (- height          (* 2 border))
        (hpbar-color ratio)]])))

(defn render
  [_ entity {:keys [ctx/world-unit-scale]}]
  (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (draw-hpbar world-unit-scale
                  (:entity/body entity)
                  ratio))))
