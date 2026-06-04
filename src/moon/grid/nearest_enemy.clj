(ns moon.grid.nearest-enemy
  (:require [moon.cell :as cell]
            [moon.faction :as faction]))

(defn nearest-enemy [grid entity]
  (cell/nearest-entity @(grid (mapv int (:body/position (:entity/body entity))))
                       (faction/enemy (:entity/faction entity))))
