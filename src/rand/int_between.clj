(ns rand.int-between)

(defn rand-int-between
  "returns a random integer between lower and upper bounds inclusive."
  ([[lower upper]]
   (rand-int-between lower upper))
  ([lower upper]
   (+ lower (rand-int (inc (- upper lower))))))
