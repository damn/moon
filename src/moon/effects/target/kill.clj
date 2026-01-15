(ns moon.effects.target.kill
  (:require [moon.effect :as effect]))

(defmethod effect/applicable? :effects.target/kill
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect/handle :effects.target/kill
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])
