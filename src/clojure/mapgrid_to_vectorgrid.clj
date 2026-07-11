(ns clojure.mapgrid-to-vectorgrid
  (:require [moon.g2d :as g2d]))

(defn f
  "Transforms a grid of {position value} to a grid2d.
  Returns [grid convert-fn]: convert-fn converts a position of the old grid to a position of the new one."
  [grid calc-newgrid-value]
  (let [posis (keys grid)
        xs (map #(% 0) posis)
        min-x (apply min xs)
        max-x (apply max xs)
        ys (map #(% 1) posis)
        min-y (apply min ys)
        max-y (apply max ys)
        width (inc (- max-x min-x))
        height (inc (- max-y min-y))
        convert (fn [[x y]] [(- x min-x -1)
                             (- y min-y -1)])]
    ; +2 so there are walls on all borders around the farthest ground cells
    [(g2d/create (+ width 2) (+ height 2)
                         (fn [[x y]]
                           ; new grid starts 1 left/top of leftest cell
                           (calc-newgrid-value (get grid [(+ x min-x -1)
                                                          (+ y min-y -1)]))))
     convert]))
