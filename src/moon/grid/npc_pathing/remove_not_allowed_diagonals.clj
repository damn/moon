(ns moon.grid.npc-pathing.remove-not-allowed-diagonals
  (:require [moon.grid.npc-pathing.is-not-allowed-diagonal :as is-not-allowed-diagonal]))

(defn f [adjacent-cells]
  (remove nil?
          (map-indexed
           (fn [idx cell]
             (when-not (or (nil? cell)
                           (is-not-allowed-diagonal/f? idx adjacent-cells))
               cell))
           adjacent-cells)))
