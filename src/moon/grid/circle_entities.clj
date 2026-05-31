(ns moon.grid.circle-entities
  (:require [com.badlogic.gdx.math.circle :as gdx-circle]
            [com.badlogic.gdx.math.intersector :as intersector]
            [moon.body :as body]
            [moon.grid.circle-to-cells :refer [circle->cells]]
            [moon.grid.cells-entities :as cells->entities]))

(defn circle->entities [g2d {:keys [position radius] :as circle}]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities/f
       (filter #(intersector/overlaps? (let [[x y] position] (gdx-circle/create x y radius))
                                       (body/rectangle (:entity/body @%))))))
