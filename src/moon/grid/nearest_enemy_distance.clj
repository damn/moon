(ns moon.grid.nearest-enemy-distance
  (:require [moon.cell.nearest-entity-distance :as nearest-entity-distance]
            [moon.faction :as faction]))

(defn nearest-enemy-distance [grid entity]
  (nearest-entity-distance/f @(grid (mapv int (:body/position (:entity/body entity))))
                             (faction/enemy (:entity/faction entity))))
