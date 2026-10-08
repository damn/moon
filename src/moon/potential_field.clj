(ns moon.potential-field
  (:require [moon.faction :as faction]
            [moon.g2d :as g2d]
            [moon.position :as position]))

(defn nearest-entity [cell faction]
  (-> cell faction :eid))

(defn nearest-entity-distance [cell faction]
  (-> cell faction :distance))

(defn cached-adjacent-cells [grid cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      position/get-8-neighbours
                      (g2d/get-cells grid))]
      (swap! cell assoc :adjacent-cells result)
      result)))

(defn nearest-enemy [grid entity]
  (nearest-entity @(grid (mapv int (:entity/position entity)))
                  (faction/enemy (:entity/faction entity))))

(defn nearest-enemy-distance [grid entity]
  (nearest-entity-distance @(grid (mapv int (:entity/position entity)))
                           (faction/enemy (:entity/faction entity))))
