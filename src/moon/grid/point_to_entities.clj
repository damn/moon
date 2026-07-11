(ns moon.grid.point-to-entities
  (:require [com.badlogic.gdx.math.rectangle :as gdx-rectangle]
            [moon.body :as body]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(gdx-rectangle/contains (body/rectangle (:entity/body @%)) (first pos) (second pos))
            (:entities @cell))))
