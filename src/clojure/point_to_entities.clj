(ns clojure.point-to-entities
  (:require [clojure.rectangle :as rectangle]
            [clojure.body.rectangle :refer [->rectangle]]))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(rectangle/contains? (->rectangle (:entity/body @%)) pos)
            (:entities @cell))))
