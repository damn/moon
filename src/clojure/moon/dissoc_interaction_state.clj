(ns clojure.moon.dissoc-interaction-state)

(defn f [ctx]
  (dissoc ctx :ctx/interaction-state))
