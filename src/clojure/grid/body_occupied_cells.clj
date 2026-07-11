(ns clojure.grid.body-occupied-cells
  (:require [moon.body :as body]
            [moon.g2d :as g2d]))

(defn body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (g2d/get-cells grid (body/touched-tiles body))
    [(grid (mapv int position))]))
