(ns clojure.moon.choose-skill
  (:require [clojure.is-useful :as useful?]
            [clojure.usable-state :as usable-state]
            [clojure.is-applicable :as applicable?]))

(defn f [ctx entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (usable-state/f % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (applicable?/f e effect-ctx)))
                          (some (fn [e] (useful?/f e effect-ctx ctx))))))
       first))
