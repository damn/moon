(ns moon.operations-test
  (:require [moon.stats.ops :as ops]
            [clojure.test :refer :all]))

(deftest add-and-remove
  (is (= (ops/add {:+ 6}
                  {:* -5 :+ -1})
         {:+ 5, :* -5}))

  (is (= (ops/remove {:+ 6 :* -50}
                     {:+ 2 :* -50})
         {:+ 4, :* 0})))

#_(deftest info
  (= (ops/info {:op/inc -4
                :op/mult 24}
               "Strength")
     "-4 Strength\n+24% Strength")

  (= (ops/info {:op/inc -4
                :op/mult 0}
               "Strength")
     "-4 Strength")

  (= (ops/info {:op/mult 35} "Hitpoints")
     "+35% Hitpoints")

  (= (ops/info {:op/inc -30
                :op/mult 5}
               "Hitpoints")
     "-30 Hitpoints\n+5% Hitpoints"))

(deftest apply-test
  (is (= (ops/apply {:op/inc 6
                     :op/mult 50}
                    10)
         24))

  (is (= (ops/apply {:op/inc -5
                     :op/mult 20}
                    10)
         6)))
