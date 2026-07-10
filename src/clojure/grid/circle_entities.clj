(ns clojure.grid.circle-entities
  (:require [gdl.math.circle :as circle]
            [gdl.math.intersector :as intersector]
            [clojure.body.rectangle :refer [->rectangle]]
            [clojure.g2d.circle-to-cells :refer [circle->cells]]
            [clojure.grid.cells-entities :as cells->entities]))

(defn circle->entities [g2d {:keys [position radius] :as circle}]
  (let [[x y] position
        gdx-circle (circle/new x y radius)]
    (->> (circle->cells g2d circle)
         (map deref)
         cells->entities/f
         (filter #(intersector/overlaps? gdx-circle
                                        (->rectangle (:entity/body @%)))))))
