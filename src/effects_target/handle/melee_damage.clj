(ns effects-target.handle.melee-damage
  (:require [game.effect :as effect]
            [moon.creature.melee-damage :as melee-damage]))

(defn f
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ applicable
  (effect/handle [:effects.target/damage (melee-damage/f @source)] effect-ctx ctx))
