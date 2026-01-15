(ns moon.effects.target.audiovisual
  (:require [moon.effect :as effect]))

(defmethod effect/applicable? :effects.target/audiovisual
  [_ {:keys [effect/target]}]
  target)

(defmethod effect/useful? :effects.target/audiovisual
  [_ _effect-ctx _world]
  false)

(defmethod effect/handle :effects.target/audiovisual
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])
