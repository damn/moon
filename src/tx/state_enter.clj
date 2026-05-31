(ns tx.state-enter
  (:require [game.state :as state]))

(defn do! [_ctx eid [state-k state-v]]
  (state/enter [state-k state-v] eid))
