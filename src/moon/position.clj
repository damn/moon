(ns moon.position)

; not using `for` because creates a lazy seq (slow)
(let [offsets [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]]]
  (defn get-8-neighbours [position]
    (mapv #(mapv + position %) offsets)))
