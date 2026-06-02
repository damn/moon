(ns moon.grid.body-occupied-cells
  (:require [moon.body :as body]
            [clojure.grid2d.get-cells :refer [get-cells]]))

(defn body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (get-cells grid (body/touched-tiles body))
    [(grid (mapv int position))]))
