(ns moon.entity.fsm
  (:require [moon.entity :as entity]
            [moon.entity.state :as state]))

(defmethod entity/after-create :entity/fsm
  [[_k {:keys [fsm initial-state]}] eid world]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  [[:tx/assoc eid :entity/fsm (assoc ((get (:world/fsms world) fsm) initial-state nil) :state initial-state)]
   [:tx/assoc eid initial-state (state/create [initial-state nil] eid world)]])
