(ns moon.grid.circle-entities
  (:require [com.badlogic.gdx.math.circle :as circle]
            [clojure.gdx.intersector.overlaps :as overlaps]
            [moon.body.rectangle :refer [->rectangle]]
            [moon.grid.circle-to-cells :refer [circle->cells]]
            [moon.grid.cells-entities :as cells->entities]))

(defn circle->entities [g2d circle]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities/f
       (filter #(overlaps/f (circle/new circle)
                            (->rectangle (:entity/body @%))))))
