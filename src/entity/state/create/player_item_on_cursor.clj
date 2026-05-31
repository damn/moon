(ns entity.state.create.player-item-on-cursor
  (:require [game.state :as state]))

(defmethod state/create :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})
