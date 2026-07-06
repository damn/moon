(ns moon.grid.point-to-entities
  (:require [clojure.gdx.rectangle.contains :as rectangle-contains?]
            [moon.body.rectangle :refer [->rectangle]]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(rectangle-contains?/f (->rectangle (:entity/body @%)) pos)
            (:entities @cell))))
