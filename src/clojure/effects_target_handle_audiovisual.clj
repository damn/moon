(ns clojure.effects-target-handle-audiovisual)

(defn f
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])
