(ns clojure.g2d.circle-to-cells
  (:require [clojure.outer-rectangle :refer [outer-rectangle]]
            [clojure.touched-tiles :refer [touched-tiles]]
            [moon.g2d :as g2d]))

(defn circle->cells [g2d circle]
  (->> circle
       outer-rectangle
       touched-tiles
       (g2d/get-cells g2d)))
