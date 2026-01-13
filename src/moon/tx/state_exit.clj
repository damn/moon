(ns moon.tx.state-exit
  (:require [moon.entity.state :as state]))

(defn do! [ctx eid [state-k state-v]]
  (state/exit [state-k state-v] eid ctx))
