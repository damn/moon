(ns clojure.ops.apply
  (:require [clojure.ops.sort :as sort]))

(defn f [ops base-value]
  (reduce (fn [base-value [k value]]
            (case k
              :op/inc  (+ base-value value)
              :op/mult (* base-value (inc (/ value 100)))))
          base-value
          (sort/f ops)))
