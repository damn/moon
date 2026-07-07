(ns clojure.body-occupied-cells
  (:require [clojure.body-touched-tiles :refer [touched-tiles]]
            [clojure.get-cells :refer [get-cells]]))

(defn body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (get-cells grid (touched-tiles body))
    [(grid (mapv int position))]))
