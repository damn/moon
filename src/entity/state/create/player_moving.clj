(ns entity.state.create.player-moving
  (:require [game.state :as state]))

(defmethod state/create :player-moving
  [[_k movement-vector] eid _ctx]
  {:movement-vector movement-vector})
