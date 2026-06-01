(ns entity.after-create.fsm
  (:require [game.constants :as constants]
            [game.state :as state]))

(defn f
  [{:keys [fsm initial-state]} eid ctx]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  [[:tx/assoc eid :entity/fsm (assoc ((case fsm
                                        :fsms/player constants/player-fsm
                                        :fsms/npc constants/npc-fsm) initial-state nil)
                                     :state initial-state)]
   [:tx/assoc eid initial-state (state/create [initial-state nil] eid ctx)]])
