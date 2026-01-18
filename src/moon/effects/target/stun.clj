(ns moon.effects.target.stun)

(defn applicable?
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defn handle
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])
