(ns draw-on-world-viewport.draw-tile-grid
  (:require [gdx.float-bits :refer [float-bits]]
            [gdx.graphics.orthographic-camera :as camera]))

(defn f
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (:viewport/camera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (:viewport/world-width world-viewport)))
      (+ 2 (int (:viewport/world-height world-viewport)))
      1
      1
      (float-bits [1 1 1 0.8])]]))
