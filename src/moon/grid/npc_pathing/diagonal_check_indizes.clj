(ns moon.grid.npc-pathing.diagonal-check-indizes
  (:require [clojure.positions :refer [positions]]
            [clojure.math.vector2.diagonal-direction :as diagonal-direction?]
            [clojure.position.get-8-neighbours :refer [get-8-neighbours]]))

(let [order (get-8-neighbours [0 0])]
  (def v
    (into {} (for [[x y] (filter diagonal-direction?/f order)]
               [(first (positions #(= % [x y]) order))
                (vec (positions #(some #{%} [[x 0] [0 y]])
                                order))]))))
