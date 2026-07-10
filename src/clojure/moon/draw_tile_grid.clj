(ns clojure.moon.draw-tile-grid
  (:require [com.badlogic.gdx.graphics.color :as color]
            [clojure.orthographic-camera-frustum :refer [frustum]]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn f
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (frustum (viewport/getCamera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (viewport/getWorldWidth world-viewport)))
      (+ 2 (int (viewport/getWorldHeight world-viewport)))
      1
      1
      (color/toFloatBits [1 1 1 0.8])]]))
