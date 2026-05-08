(ns moon.application.render.draw-on-world-viewport.draw-tile-grid
  (:require [com.badlogic.gdx.graphics.color :as color]
            [clojure.gdx.utils.viewport :as viewport]
            [moon.camera :as camera]))

(defn draws
  [ctx]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum ctx)]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (viewport/world-width  (:ctx/world-viewport ctx))))
      (+ 2 (int (viewport/world-height (:ctx/world-viewport ctx))))
      1
      1
      (color/float-bits [1 1 1 0.8])]]))
