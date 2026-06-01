(ns moon.grid.npc-pathing.diagonal-check-indizes
  (:require [clojure.coll.positions :as positions]
            [clojure.math.vector2 :as v]
            [clojure.math.position :as position]))

(let [order (position/get-8-neighbours [0 0])]
  (def v
    (into {} (for [[x y] (filter v/diagonal-direction? order)]
               [(first (positions/f #(= % [x y]) order))
                (vec (positions/f #(some #{%} [[x 0] [0 y]])
                                  order))]))))
