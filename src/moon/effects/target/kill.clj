(ns moon.effects.target.kill)

(defn applicable?
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defn handle
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])
