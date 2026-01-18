(ns moon.entity.state.stunned
  (:require [moon.timer :as timer]))

(defn create
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (timer/create elapsed-time duration)})

(defn tick
  [[_k {:keys [counter]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))

(defn render
  [_ {:keys [entity/body]} _ctx]
  [[:draw/circle
    (:body/position body)
    0.5
    [1 1 1 0.6]]])

(defn cursor
  [_ _eid _ctx]
  :cursors/denied)

(defn pause-game?
  [_]
  false)
