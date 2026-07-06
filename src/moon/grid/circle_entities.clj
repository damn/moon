(ns moon.grid.circle-entities
  (:require [com.badlogic.gdx.math.circle :as circle]
            [com.badlogic.gdx.math.intersector :as intersector]
            [moon.body.rectangle :refer [->rectangle]]
            [moon.grid.circle-to-cells :refer [circle->cells]]
            [moon.grid.cells-entities :as cells->entities]))

(defn circle->entities [g2d circle]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities/f
       (filter #(intersector/overlaps (circle/new circle)
                                      (->rectangle (:entity/body @%))))))
