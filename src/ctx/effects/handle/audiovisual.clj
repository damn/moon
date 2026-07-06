(ns ctx.effects.handle.audiovisual)

(defn f
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])
