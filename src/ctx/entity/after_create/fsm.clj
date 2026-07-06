(ns ctx.entity.after-create.fsm
  (:require [ctx.create-fsm :refer [create-fsm]]
            [moon.create-entity-state :as create-entity-state]))

(defn f
  [{:keys [fsm initial-state]} eid ctx]
  [[:tx/assoc eid :entity/fsm (create-fsm ctx fsm initial-state)]
   [:tx/assoc eid initial-state (create-entity-state/f [initial-state nil] eid ctx)]])
