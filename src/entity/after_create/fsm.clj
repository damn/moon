(ns entity.after-create.fsm
  (:require [game.state :as state]))

(defn f
  [{:keys [fsm initial-state]} eid {:keys [ctx/fsms]
                                    :as ctx}]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  [[:tx/assoc eid :entity/fsm (assoc ((case fsm
                                        :fsms/player (:player fsms)
                                        :fsms/npc (:npc fsms))
                                      initial-state
                                      nil)
                                     :state initial-state)]
   [:tx/assoc eid initial-state (state/create [initial-state nil] eid ctx)]])
