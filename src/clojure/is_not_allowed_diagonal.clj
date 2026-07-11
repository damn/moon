(ns clojure.is-not-allowed-diagonal
  (:require [clojure.coll :refer [positions]]
            [moon.position :as position]))

; ?? inline

(let [order (position/get-8-neighbours [0 0])
      diagonal? (fn [[^int x ^int y]]
                  (and (not (zero? x))
                       (not (zero? y))))]
  (def diagonal-check-indizes
    (into {} (for [[x y] (filter diagonal? order)]
               [(first (positions #(= % [x y]) order))
                (vec (positions #(some #{%} [[x 0] [0 y]])
                                order))]))))

(defn f? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))
