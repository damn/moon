(ns moon.grid.circle-entities
  (:require [clojure.math.circle.overlaps :refer [overlaps?]]
            [moon.body.rectangle :refer [->rectangle]]
            [moon.grid.circle-to-cells :refer [circle->cells]]
            [moon.grid.cells-entities :as cells->entities]))

(defn circle->entities [g2d circle]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities/f
       (filter #(overlaps? circle
                           (->rectangle (:entity/body @%))))))
