(ns moon.stats.ops-test
  (:require [clojure.ops-add :as add]
            [clojure.ops-remove :as remove]
            [clojure.ops.apply :as apply]
            [clojure.test :refer :all]))

(deftest add-and-remove
  (is (= (add/f {:+ 6}
                  {:* -5 :+ -1})
         {:+ 5, :* -5}))

  (is (= (remove/f {:+ 6 :* -50}
                     {:+ 2 :* -50})
         {:+ 4, :* 0})))

(deftest apply-test
  (is (= (apply/f {:op/inc 6
                     :op/mult 50}
                    10)
         24))

  (is (= (apply/f {:op/inc -5
                     :op/mult 20}
                    10)
         6)))
