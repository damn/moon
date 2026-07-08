(ns clojure.moon.create-game-config
  (:require [clojure.moon.create-colors :refer [colors]]
            [clojure.moon.create-controls :refer [controls]]
            [clojure.moon.create-controls-info :refer [controls-info]]
            [clojure.moon.create-max-speed :refer [max-speed]]
            [clojure.moon.create-render-z-order :refer [render-z-order]]))

(defn f [ctx]
  (-> ctx
      (assoc :ctx/controls controls)
      (assoc :ctx/controls-info controls-info)
      (assoc :ctx/colors colors)
      (assoc :ctx/render-z-order render-z-order)
      (assoc :ctx/max-speed max-speed)))
