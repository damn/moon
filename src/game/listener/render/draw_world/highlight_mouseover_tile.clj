(ns game.listener.render.draw-world.highlight-mouseover-tile
  (:require [game.shared :refer [colors
                                 draw-fn-rectangle]]
            [moon.world :as world]))

(defn highlight-mouseover-tile
  [ctx shape-drawer world-mouse-position world]
  (let [world @world
        [x y] (mapv int world-mouse-position)
        cell (world/cell-at world [x y])]
    (when (and cell (#{:air :none} (:movement cell)))
      (draw-fn-rectangle shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (:colors/mouseover-tile-air colors)
                           :none (:colors/mouseover-tile-none colors))))))
