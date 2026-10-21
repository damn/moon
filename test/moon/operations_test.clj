(ns moon.operations-test
  (:require [lib.mods :as mods]
            [clojure.test :refer :all]))

(deftest add-and-remove
  (is (= (mods/add {:modifier/x {:+ 6}}
                   {:modifier/x {:* -5 :+ -1}})
         {:modifier/x {:+ 5, :* -5}}))

  (is (= (mods/remove {:modifier/x {:+ 6 :* -50}}
                      {:modifier/x {:+ 2 :* -50}})
         {:modifier/x {:+ 4, :* 0}})))

(deftest apply-test
  (is (= (mods/get-value 10
                         {:modifier/x {:op/inc 6
                                       :op/mult 50}}
                         :modifier/x)
         24))

  (is (= (mods/get-value 10
                         {:modifier/x {:op/inc -5
                                       :op/mult 20}}
                         :modifier/x)
         6)))
