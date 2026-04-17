(ns moon.draw-on-world-viewport.draw-tile-grid
  (:require [clojure.viewport :as viewport]
            [clojure.camera :as camera]))

(defn draws
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (viewport/camera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (viewport/world-width  world-viewport)))
      (+ 2 (int (viewport/world-height world-viewport)))
      1
      1
      [1 1 1 0.8]]]))
