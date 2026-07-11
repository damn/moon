(ns moon.ops
  (:refer-clojure :exclude [remove apply sort]))

(defn add [ops other-ops]
  (merge-with + ops other-ops))

(defn remove [ops other-ops]
  (merge-with - ops other-ops))

(defn sort [ops]
  (clojure.core/sort-by (fn [[k]]
                          (case k
                            :op/inc 0
                            :op/mult 1))
                        ops))

(defn apply [ops base-value]
  (reduce (fn [base-value [k value]]
            (case k
              :op/inc  (+ base-value value)
              :op/mult (* base-value (inc (/ value 100)))))
          base-value
          (sort ops)))
