(ns moon.potential-field
  (:require [moon.faction :as faction]
            [moon.position :as position]))

(defn nearest-entity [cell faction]
  (-> cell faction :eid))

(defn nearest-entity-distance [cell faction]
  (-> cell faction :distance))

(defn cached-adjacent-cells [grid cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (into [] (keep grid) (position/get-8-neighbours (:position @cell)))]
      (swap! cell assoc :adjacent-cells result)
      result)))

(defn nearest-enemy [grid entity]
  (nearest-entity @(grid (mapv int (:entity/position entity)))
                  (faction/enemy (:entity/faction entity))))

(defn nearest-enemy-distance [grid entity]
  (nearest-entity-distance @(grid (mapv int (:entity/position entity)))
                           (faction/enemy (:entity/faction entity))))
