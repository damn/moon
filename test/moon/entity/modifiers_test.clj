(ns moon.entity.modifiers-test
  (:require [clojure.test :refer :all]
            #_[forge.entity.modifiers :as mods]))

#_(deftest add-mods
  (is (= (mods/add {:modifier/movement-speed {:op/mult 10}}
                   {:modifier/movement-speed {:op/mult -10}})
         {:modifier/movement-speed {:op/mult 0}}))

  (is (= (mods/add {:modifier/strength {:op/inc 3}}
                   {:modifier/movement-speed {:op/mult -1}})
         {:modifier/strength {:op/inc 3}
          :modifier/movement-speed {:op/mult -1}})))

#_(deftest remove-mods
  (is (= (mods/remove {:modifier/movement-speed {:op/mult 50}}
                      {:modifier/movement-speed {:op/mult -10}})
         {:modifier/movement-speed {:op/mult 60}})))

#_(deftest handler
  (let [mods     {:modifier/movement-speed {:op/mult [0.1]}}
        new-mods {:modifier/movement-speed {:op/mult -0.1}}
        resulting-mods (mods/add mods new-mods)
        eid (atom {:stats/modifiers mods})]

    (= (component/handle [:stats/modifiers
                          eid
                          :add
                          new-mods])
       [[:e/assoc
         eid
         :stats/modifiers
         resulting-mods]])))

#_(deftest apply-modifiers
  (let [eid (atom
             {:stats/modifiers
              {:modifier/movement-speed
               {:op/mult [0.1]}}})]
    (component/->handle [[:stats/modifiers
                          eid
                          :add
                          {:modifier/movement-speed {:op/mult -0.1}}]])
    (is (= (:modifier/movement-speed (:stats/modifiers @eid))
           #:op{:mult [0.1 -0.1]}))))

#_(deftest reverse-modifiers
  (let [eid (atom {:stats/modifiers {:modifier/movement-speed {:op/mult [0.1 -0.1]}}})]
    (component/->handle
     [[:stats/modifiers
       eid
       :remove
       {:modifier/movement-speed {:op/mult -0.1}}]])
    (is (= (:modifier/movement-speed (:stats/modifiers @eid))
           #:op{:mult [0.1]}))))

#_(deftest test-modified-value
  (is (= (entity/modified-value {:stats/modifiers {:modifier/damage-deal {:op/val-inc [30]
                                                                           :op/val-mult [0.5]}}}
                                :modifier/damage-deal
                                [5 10])
         [52 52]))

  (is (= (entity/modified-value {:stats/modifiers {:modifier/damage-deal {:op/val-inc [30]}
                                                    :entity/fooz-barz {:op/babu [1 2 3]}}}
                                :modifier/damage-deal
                                [5 10])
         [35 35]))

  (is (= (entity/modified-value {:stats/modifiers {}}
                                :modifier/damage-deal [5 10])
         [5 10]))

  (is (= (entity/modified-value {:stats/modifiers {:modifier/hp {:op/max-inc [10 1]
                                                                  :op/max-mult [0.5]}}}
                                :modifier/hp
                                [100 100])
         [100 166]))

  (is (= (entity/modified-value {:stats/modifiers {:modifier/movement-speed {:op/inc [2]
                                                                              :op/mult [0.1 0.2]}}}
                                :modifier/movement-speed
                                3)
         6.5)))
