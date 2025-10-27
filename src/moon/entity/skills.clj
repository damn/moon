(ns moon.entity.skills
  (:require [moon.effect :as effect]
            [moon.entity.skills.skill :as skill]
            [moon.entity.stats :as stats]
            [moon.timer :as timer]))

; this is my game/world logic !
(extend-type clojure.lang.PersistentHashMap
  skill/Skill
  (usable-state [{:keys [skill/cooling-down? skill/effects] :as skill}
                 entity
                 effect-ctx]
    (cond
     cooling-down?
     :cooldown

     (stats/not-enough-mana? (:entity/stats entity) skill)
     :not-enough-mana

     (not (seq (filter #(effect/applicable? % effect-ctx) effects)))
     :invalid-params

     :else
     :usable)))

(defn create! [skills eid _world]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))

(defn set-cooldown [skills skill elapsed-time]
  (assoc-in skills
            [(:property/id skill) :skill/cooling-down?]
            (timer/create elapsed-time (:skill/cooldown skill))))
