(ns entity.state.enter.npc-dead
  (:require [game.state :as state]))

(defmethod state/enter :npc-dead
  [_ eid]
  [[:tx/mark-destroyed eid]])
