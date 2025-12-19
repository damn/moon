(ns moon.application.render.draw-on-world-viewport.draw-tile-grid
  (:require [moon.graphics :as graphics]))

(def ^:dbg-flag show-tile-grid? false)

(defn do!
  [{:keys [ctx/graphics]}]
  (when show-tile-grid?
    (let [[left-x _right-x bottom-y _top-y] (graphics/frustum graphics)]
      [[:draw/grid
        (int left-x)
        (int bottom-y)
        (inc (int (graphics/world-vp-width  graphics)))
        (+ 2 (int (graphics/world-vp-height graphics)))
        1
        1
        [1 1 1 0.8]]])))
