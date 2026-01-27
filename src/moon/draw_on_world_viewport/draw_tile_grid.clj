(ns moon.draw-on-world-viewport.draw-tile-grid
  (:require [moon.camera :as camera])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn draws
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (Viewport/.getCamera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (Viewport/.getWorldWidth  world-viewport)))
      (+ 2 (int (Viewport/.getWorldHeight world-viewport)))
      1
      1
      [1 1 1 0.8]]]))
