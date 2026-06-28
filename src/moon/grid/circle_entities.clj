(ns moon.grid.circle-entities
  (:require [math.circle :as circle]
            [moon.body.rectangle :refer [->rectangle]]
            [moon.grid.circle-to-cells :refer [circle->cells]]
            [moon.grid.cells-entities :as cells->entities])
  (:import (com.badlogic.gdx.math Intersector)))

(defn circle->entities [g2d circle]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities/f
       (filter #(Intersector/overlaps (circle/create circle)
                                      (->rectangle (:entity/body @%))))))
