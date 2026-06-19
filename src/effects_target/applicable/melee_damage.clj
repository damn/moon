(ns effects-target.applicable.melee-damage
  (:require [game.effect :as effect]
            [moon.creature.melee-damage :as melee-damage]))

(defn f
  [_ {:keys [effect/source] :as effect-ctx}]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ handle
  (effect/applicable? [:effects.target/damage (melee-damage/f @source)] effect-ctx))
