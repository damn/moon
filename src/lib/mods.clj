(ns lib.mods
  (:refer-clojure :exclude [remove])
  (:require [clojure.math :as math]
            [clojure.string :as str]))

(defn- add-ops [ops other-ops]
  (merge-with + ops other-ops))

(defn- remove-ops [ops other-ops]
  (merge-with - ops other-ops))

(defn- sort-ops [ops]
  (sort-by (fn [[k]]
             (case k
               :op/inc 0
               :op/mult 1))
           ops))

(defn- apply-ops [ops base-value]
  (reduce (fn [base-value [k value]]
            (case k
              :op/inc  (+ base-value value)
              :op/mult (* base-value (inc (/ value 100)))))
          base-value
          (sort-ops ops)))

(defn add [mods other-mods]
  (merge-with add-ops mods other-mods))

(defn remove [mods other-mods]
  (merge-with remove-ops mods other-mods))

(defn get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (apply-ops (modifier-k modifiers)
             base-value))

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
                                      (sort-ops modifier-ops))))
                    mods))))
