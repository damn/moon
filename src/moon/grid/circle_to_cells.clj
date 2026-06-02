(ns moon.grid.circle-to-cells
  (:require [clojure.math.circle :as circle]
            [clojure.math.rectangle :as rectangle]
            [clojure.grid2d.get-cells :refer [get-cells]]))

(defn circle->cells [g2d circle]
  (->> circle
       circle/outer-rectangle
       rectangle/touched-tiles
       (get-cells g2d)))
