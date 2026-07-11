(ns clojure.g2d.circle-to-cells
  (:require [moon.circle :as circle]
            [moon.rectangle :as rectangle]
            [moon.g2d :as g2d]))

(defn circle->cells [g2d circle]
  (->> circle
       circle/outer-rectangle
       rectangle/touched-tiles
       (g2d/get-cells g2d)))
