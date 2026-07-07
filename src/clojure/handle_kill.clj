(ns clojure.handle-kill)

(defn f
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])
