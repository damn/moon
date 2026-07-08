(ns clojure.circle-entities
  (:require [clojure.gdx.math.circle :as circle]
            [clojure.intersector :as intersector]
            [clojure.body.rectangle :refer [->rectangle]]
            [clojure.circle-to-cells :refer [circle->cells]]
            [clojure.cells-entities :as cells->entities]))

(defn circle->entities [g2d circle]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities/f
       (filter #(intersector/overlaps? (circle/new circle)
                                      (->rectangle (:entity/body @%))))))
