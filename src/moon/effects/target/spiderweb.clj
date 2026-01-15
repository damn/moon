(ns moon.effects.target.spiderweb
  (:require [moon.effect :as effect]
            [moon.entity.stats :as stats]
            [moon.timer :as timer]))

(def ^:private spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def ^:private spiderweb-duration 5)

(defmethod effect/applicable? :effects.target/spiderweb
  [_ {:keys [effect/target]}]
  ; TODO has stats , for mod-add
  ; e,g, spiderweb on projectile leads to error
  (:entity/stats @target))

(defmethod effect/handle :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/world]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (let [{:keys [world/elapsed-time]} world]
    (when-not (:entity/temp-modifier @target)
      [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                :counter (timer/create elapsed-time spiderweb-duration)}]
       [:tx/update target :entity/stats stats/add spiderweb-modifiers]])))
