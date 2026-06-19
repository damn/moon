(ns effects-target.handle.stun)

(defn f
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])
