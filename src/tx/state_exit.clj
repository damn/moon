(ns tx.state-exit
  (:require [game.state :as state]))

(defn do! [ctx eid [state-k state-v]]
  (state/exit [state-k state-v] eid ctx))
