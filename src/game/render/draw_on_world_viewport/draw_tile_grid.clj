(ns game.render.draw-on-world-viewport.draw-tile-grid
  (:require [clojure.graphics.color :as color]
            [game.ctx :as ctx]))

(defn draws
  [ctx]
  (let [[left-x _right-x bottom-y _top-y] (ctx/camera-frustum ctx)]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (ctx/world-viewport-width ctx)))
      (+ 2 (int (ctx/world-viewport-height ctx)))
      1
      1
      (color/float-bits [1 1 1 0.8])]]))
