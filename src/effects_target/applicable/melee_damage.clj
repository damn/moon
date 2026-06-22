(ns effects-target.applicable.melee-damage
  (:require [moon.effect.is-applicable :as applicable?]
            [moon.creature.melee-damage :as melee-damage]))

(defn f
  [_ {:keys [effect/source] :as effect-ctx}]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ handle
  (applicable?/f [:effects.target/damage (melee-damage/f @source)] effect-ctx))
