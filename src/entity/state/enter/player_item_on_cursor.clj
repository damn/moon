(ns entity.state.enter.player-item-on-cursor
  (:require [game.state :as state]))

(defmethod state/enter :player-item-on-cursor
  [[_k {:keys [item]}] eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])
