(ns moon.draw-on-world-viewport.highlight-mouseover-tile
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]))

(defn draws
  [{:keys [ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  (color/float-bits [1 1 0 0.5])
          :none (color/float-bits [1 0 0 0.5]))]])))
