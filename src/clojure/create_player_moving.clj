(ns clojure.create-player-moving)

(defn f
  [[_k movement-vector] eid _ctx]
  {:movement-vector movement-vector})
