(ns moon.rand)

(defn int-between
  "returns a random integer between lower and upper bounds inclusive."
  ([[lower upper]]
   (int-between lower upper))
  ([lower upper]
   (+ lower (rand-int (inc (- upper lower))))))
