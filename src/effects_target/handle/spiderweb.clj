(ns effects-target.handle.spiderweb
  (:require [moon.stats.add-mods :as add-mods]
            [game.constants :refer [spiderweb-modifiers spiderweb-duration]]
            [clojure.timer.create :refer [create-timer]]))

(defn f
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (create-timer elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats add-mods/f spiderweb-modifiers]]))
