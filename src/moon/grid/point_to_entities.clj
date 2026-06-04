(ns moon.grid.point-to-entities
  (:refer-clojure :exclude [contains?])
  (:require [clojure.gdx.math.rectangle :refer [contains?]]
            [moon.body.rectangle :refer [->rectangle]]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(contains? (->rectangle (:entity/body @%)) pos)
            (:entities @cell))))
