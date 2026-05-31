(ns effect.applicable.spawn
  (:require [game.effect :as effect]))

(defmethod effect/applicable? :effects/spawn
  [_ {:keys [effect/source effect/target-position]}]
  (and (:entity/faction @source)
       target-position))
