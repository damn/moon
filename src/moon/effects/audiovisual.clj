(ns moon.effects.audiovisual
  (:require [moon.effect :as effect]))

(defmethod effect/applicable? :effects/audiovisual
  [_ {:keys [effect/target-position]}]
  target-position)

(defmethod effect/useful? :effects/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod effect/handle :effects/audiovisual
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])
