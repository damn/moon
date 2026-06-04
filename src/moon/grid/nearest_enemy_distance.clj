(ns moon.grid.nearest-enemy-distance
  (:require [moon.cell :as cell]
            [moon.faction :as faction]))

(defn nearest-enemy-distance [grid entity]
  (cell/nearest-entity-distance @(grid (mapv int (:body/position (:entity/body entity))))
                                (faction/enemy (:entity/faction entity))))
