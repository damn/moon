(ns moon.tx.state-enter
  (:require [moon.state :as state]))

(defn do! [_ctx eid [state-k state-v]]
  (state/enter [state-k state-v] eid))
