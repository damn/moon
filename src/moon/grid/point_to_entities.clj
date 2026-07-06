(ns moon.grid.point-to-entities
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]
            [moon.body.rectangle :refer [->rectangle]]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(rectangle/contains? (->rectangle (:entity/body @%)) pos)
            (:entities @cell))))
