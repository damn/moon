(ns moon.grid.circle-to-cells
  (:require [clojure.math.circle :as circle]
            [clojure.math.rectangle :as rectangle]
            [moon.grid2d :as g2d]))

(defn circle->cells [g2d circle]
  (->> circle
       circle/outer-rectangle
       rectangle/touched-tiles
       (g2d/get-cells g2d)))
