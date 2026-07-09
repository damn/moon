(ns clojure.moon.after-create-component.after-create-fsm
  (:require [clojure.moon.fsms :refer [fsms]]
            [clojure.moon.create-entity-state :as create-entity-state]))

(defn create-fsm
  [fsm initial-state]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))

(defn f
  [{:keys [fsm initial-state]} eid ctx]
  [[:tx/assoc eid :entity/fsm (create-fsm fsm initial-state)]
   [:tx/assoc eid initial-state (create-entity-state/f [initial-state nil] eid ctx)]])
