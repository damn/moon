(ns moon.damage-test
  (:require [clojure.calc-damage :as calc-damage]
            [clojure.test :refer :all]))

(def get-damage calc-damage/f)

(deftest modify-source-damage
  (is (= (get-damage {:stats/modifiers {:modifier/damage-deal-min {:op/inc 1
                                                                    :op/mult 50}}}
                     {:damage/min-max [5 10]})
         #:damage{:min-max [9 10]})))

(deftest modify-source-damage-align
  (is (= (get-damage {:stats/modifiers {:modifier/damage-deal-min {:op/inc 20}}}
                     {:damage/min-max [5 10]})
         #:damage{:min-max [25 25]})))

(deftest modify-source-damage-align-negative
  (is (= (get-damage {:stats/modifiers {:modifier/damage-deal-min {:op/mult -100}
                                         :modifier/damage-deal-max {:op/inc 5}}}
                     {:damage/min-max [5 10]})
         #:damage{:min-max [0 15]})))

(deftest modify-damage
  ; fails
  (is (= (get-damage {:stats/modifiers {:modifier/damage-deal-min {:op/mult -100}
                                         :modifier/damage-deal-max {:op/inc 5}}}
                     {:stats/modifiers {:modifier/damage-receive-max {:op/mult -50}}}
                     {:damage/min-max [5 10]})
         ; returns [5 5] FIXME
         #:damage{:min-max [0 7]})))

(deftest max-reduces-min
  (is (= (get-damage {}
                     {:stats/modifiers {:modifier/damage-receive-max {:op/mult -50}}}
                     {:damage/min-max [5 5]})
         #:damage{:min-max [2 2]})))

(deftest max-decrease-lowers-min
  (is (= (get-damage
          {:stats/modifiers {:modifier/damage-deal-max {:op/inc -5}}}
          {:damage/min-max [8 9]})
         {:damage/min-max [4 4]})))

(deftest min-increase-increases-max
  (is (= (get-damage
          {:stats/modifiers {:modifier/damage-deal-min {:op/inc 5}}}
          {:damage/min-max [8 9]})
         {:damage/min-max [13 13]})))

(deftest does-not-reduce-below-zero
  (is (= (get-damage
          {:stats/modifiers {:modifier/damage-deal-min {:op/inc -6}}}
          {:damage/min-max [5 10]})
         #:damage{:min-max [0 10]})))
