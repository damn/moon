(ns moon.entity.state.npc-dead
  (:require [moon.entity.state :as state]))

(defmethod state/enter :npc-dead
  [_ eid]
  [[:tx/mark-destroyed eid]])
