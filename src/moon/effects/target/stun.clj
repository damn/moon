(ns moon.effects.target.stun
  (:require [moon.effect :as effect]))

(defmethod effect/applicable? :effects.target/stun
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect/handle :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])
