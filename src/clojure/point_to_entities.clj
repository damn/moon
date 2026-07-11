(ns clojure.point-to-entities
  (:require [gdx.math.rectangle :as rectangle]
            [moon.body :as body]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(rectangle/contains? (body/rectangle (:entity/body @%)) pos)
            (:entities @cell))))
