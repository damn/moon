(ns effect.target.audiovisual)

(defn applicable?
  [_ {:keys [effect/target]}]
  target)

(defn useful?
  [_ _effect-ctx _ctx]
  false)

(defn handle
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])
