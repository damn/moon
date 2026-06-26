(ns moon.records.grid-cell)

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
