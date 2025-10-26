(ns cdq.entity.state)

(defprotocol State
  (create       [_ eid world])
  (enter        [_ eid])
  (exit         [_ eid ctx]))
