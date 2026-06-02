(ns entity.state.create.stunned
  (:require [game.state :as state]
            [clojure.timer.create :refer [create-timer]]))

(defmethod state/create :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (create-timer elapsed-time duration)})
