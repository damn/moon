(ns clojure.is-not-allowed-diagonal
  (:require [clojure.positions :refer [positions]]
            [clojure.diagonal-direction :as diagonal-direction?]
            [clojure.get-8-neighbours :refer [get-8-neighbours]]))

(let [order (get-8-neighbours [0 0])]
  (def diagonal-check-indizes
    (into {} (for [[x y] (filter diagonal-direction?/f order)]
               [(first (positions #(= % [x y]) order))
                (vec (positions #(some #{%} [[x 0] [0 y]])
                                order))]))))

(defn f? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))
