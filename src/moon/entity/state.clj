(ns moon.entity.state) ; moon.creature.state ? / moon.player-entity.state ?

(defprotocol State
  (create       [_ eid world])
  (enter        [_ eid])
  (exit         [_ eid ctx]))
