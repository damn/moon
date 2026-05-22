(ns game.state.stunned
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :stunned
  [[_k {:keys [counter]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))

(defmethod entity/render :stunned
  [_ {:keys [entity/body]} {:keys [ctx/colors]}]
  [[:draw/circle
    (:body/position body)
    0.5
    (:colors/stunned colors)]])
