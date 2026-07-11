(ns clojure.nearest-enemy
  (:require [clojure.nearest-entity :as nearest-entity]
            [moon.faction :as faction]))

(defn nearest-enemy [grid entity]
  (nearest-entity/f @(grid (mapv int (:body/position (:entity/body entity))))
                    (faction/enemy (:entity/faction entity))))
