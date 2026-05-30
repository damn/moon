(ns entity.state.create.stunned
  (:require [game.state :as state]
            [moon.timer :as timer]))

(defmethod state/create :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (timer/create elapsed-time duration)})
