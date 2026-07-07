(ns ctx.effects-target.handle.spiderweb
  (:require [moon.stats.add-mods :as add-mods]
            [clojure.timer-create :refer [create-timer]]))

(let [spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}}
      spiderweb-duration 5]
  (defn f
    [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
    ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
    (when-not (:entity/temp-modifier @target)
      [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                :counter (create-timer elapsed-time spiderweb-duration)}]
       [:tx/update target :entity/stats add-mods/f spiderweb-modifiers]])))
