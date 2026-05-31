(ns effect.applicable.projectile
  (:require [game.effect :as effect]))

(defmethod effect/applicable? :effects/projectile
  [_ {:keys [effect/target-direction]}]
  ; TODO for npcs need target -- anyway only with direction
  ; faction @ source also ?
  target-direction)
