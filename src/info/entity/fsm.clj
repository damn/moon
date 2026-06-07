(ns info.entity.fsm)

(defn f [fsm _ctx]
  (str "State: " (name (:state fsm))))
