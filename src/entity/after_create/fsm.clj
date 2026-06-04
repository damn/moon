(ns entity.after-create.fsm
  (:require [game.ctx.create-fsm :refer [create-fsm]]
            [game.state :as state]))

(defn f
  [{:keys [fsm initial-state]} eid ctx]
  [[:tx/assoc eid :entity/fsm (create-fsm ctx fsm initial-state)]
   [:tx/assoc eid initial-state (state/create [initial-state nil] eid ctx)]])
