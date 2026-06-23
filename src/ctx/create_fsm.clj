(ns ctx.create-fsm)

(defn create-fsm
  [{:keys [ctx/fsms]
    :as ctx}
   fsm
   initial-state]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))
