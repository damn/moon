(ns moon.mods
  (:refer-clojure :exclude [remove])
  (:require [clojure.math :as math]
            [clojure.string :as str]
            [moon.ops :as ops]))

(defn add [mods other-mods]
  (merge-with ops/add mods other-mods))

(defn remove [mods other-mods]
  (merge-with ops/remove mods other-mods))

(defn format-text
  [mods]
  (when (seq mods)
    (str/join "\n"
              (keep (fn [[k modifier-ops]]
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
                                      (ops/sort modifier-ops))))
                    mods))))
