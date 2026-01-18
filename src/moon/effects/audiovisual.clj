(ns moon.effects.audiovisual)

(defn applicable?
  [_ {:keys [effect/target-position]}]
  target-position)

(defn useful?
  [_ _effect-ctx _ctx]
  false)

(defn handle
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])
