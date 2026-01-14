(ns moon.entity.state.stunned
  (:require [moon.entity :as entity]
            [moon.entity.state :as state]
            [moon.timer :as timer]))

(defmethod state/create :stunned
  [[_k duration] _eid {:keys [world/elapsed-time]}]
  {:counter (timer/create elapsed-time duration)})

(defmethod entity/tick :stunned
  [[_k {:keys [counter]}] eid {:keys [world/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))

(defmethod entity/render :stunned
  [_ {:keys [entity/body]} _ctx]
  [[:draw/circle
    (:body/position body)
    0.5
    [1 1 1 0.6]]])

(defmethod state/cursor :stunned
  [_ _eid _ctx]
  :cursors/denied)
