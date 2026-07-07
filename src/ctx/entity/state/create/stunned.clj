(ns ctx.entity.state.create.stunned
  (:require [clojure.timer-create :refer [create-timer]]))

(defn f
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (create-timer elapsed-time duration)})
