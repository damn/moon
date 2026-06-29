(ns moon.ops.info
  (:require [clojure.math :as math]
            [clojure.string :as str]
            [moon.ops.sort :as sort]))

(defn f [ops modifier-k]
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
             (sort/f ops))))
