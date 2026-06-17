(ns effect.applicable.damage
  (:require [game.effect :as effect]))

(defmethod effect/applicable? :effects.target/damage
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target))) ; not exist anymore ... bugfix .... -> is 'creature?'
