(ns moon.mods
  (:require [clojure.math :as math]
            [clojure.ops.sort :as sort]
            [clojure.string :as str]))

(defn format-text
  [mods]
  (when (seq mods)
    (str/join "\n"
              (keep (fn [[k ops]]
                      (str/join "\n"
                                (keep (fn [[op-k v]]
                                        (when-not (zero? v)
                                          (str (case (math/signum v)
                                                 0.0 ""
                                                 1.0 "+"
                                                 -1.0 "")
                                               (case op-k
                                                 :op/inc  (str v)
                                                 :op/mult (str v "%"))
                                               " "
                                               (str/capitalize (name k)))))
                                      (sort/f ops))))
                    mods))))
