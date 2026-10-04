(ns moon.stats.ops-test
  (:require [moon.game :as game]
            [clojure.test :refer :all]))
(deftest add-and-remove
  (is (= (game/ops-add {:+ 6}
                  {:* -5 :+ -1})
         {:+ 5, :* -5}))

  (is (= (game/ops-remove {:+ 6 :* -50}
                     {:+ 2 :* -50})
         {:+ 4, :* 0})))

(deftest apply-test
  (is (= (game/ops-apply {:op/inc 6
                     :op/mult 50}
                    10)
         24))

  (is (= (game/ops-apply {:op/inc -5
                     :op/mult 20}
                    10)
         6)))
