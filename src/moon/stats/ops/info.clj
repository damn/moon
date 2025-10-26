(ns moon.stats.ops.info
  (:require [moon.stats.ops :as ops]
            [clojure.string :as str]
            [clojure.math :as math]))

(defn info [ops modifier-k]
  (str/join "\n"
            (keep
             (fn [[k v]]
               (when-not (zero? v)
                 (str (case (math/signum v)
                        0.0 ""
                        1.0 "+"
                        -1.0 "")
                      (case k
                        :op/inc  (str v)
                        :op/mult (str v "%"))
                      " "
                      (str/capitalize (name modifier-k)))))
             (ops/sort ops))))

(comment
 (deftest info-texts
   (is (= (ops/info-text {:op/inc -4
                          :op/mult 24}
                         "Strength")
          "-4 Strength\n+24% Strength"))

   (is (= (ops/info-text {:op/inc -4
                          :op/mult 0}
                         "Strength")
          "-4 Strength"))

   (is (= (ops/info-text {:op/mult 35}
                         "Hitpoints")
          "+35% Hitpoints"))

   (is (= (ops/info-text {:op/inc -30
                          :op/mult 5}
                         "Hitpoints")
          "-30 Hitpoints\n+5% Hitpoints")))
 )
