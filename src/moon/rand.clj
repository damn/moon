(ns moon.rand)

(defn int-between
  "returns a random integer between lower and upper bounds inclusive."
  ([[lower upper]]
   (int-between lower upper))
  ([lower upper]
   (+ lower (rand-int (inc (- upper lower))))))

(defn get-rand-weighted-item
  "given a sequence of items and their weight, returns a weighted random item.
  for example {:a 5 :b 1} returns b only in about 1 of 6 cases"
  [weights]
  (let [result (rand-int (reduce + (map #(% 1) weights)))]
    (loop [r 0
           items weights]
      (let [[item weight] (first items)
            r (+ r weight)]
        (if (> r result)
          item
          (recur (int r) (rest items)))))))
