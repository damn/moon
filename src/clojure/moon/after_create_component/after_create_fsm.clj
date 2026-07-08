(ns clojure.moon.after-create-component.after-create-fsm
  (:require [clojure.create-fsm :refer [create-fsm]]
            [clojure.create-entity-state :as create-entity-state]))

(defn f
  [{:keys [fsm initial-state]} eid ctx]
  [[:tx/assoc eid :entity/fsm (create-fsm fsm initial-state)]
   [:tx/assoc eid initial-state (create-entity-state/f [initial-state nil] eid ctx)]])
