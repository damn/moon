(ns clojure.entity-fsm)

(defn f [fsm _ctx]
  (str "State: " (name (:state fsm))))
