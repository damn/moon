(ns clojure.grid-cell)

(defrecord R [
              position
              middle
              adjacent-cells
              movement
              entities
              occupied
              good
              evil
              ])
