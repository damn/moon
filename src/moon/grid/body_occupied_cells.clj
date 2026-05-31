(ns moon.grid.body-occupied-cells
  (:require [moon.body :as body]
            [moon.grid2d :as g2d]))

; (g2d/get-cells grid (body/occupied-tiles body))
(defn body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (g2d/get-cells grid (body/touched-tiles body))
    [(grid (mapv int position))]))
