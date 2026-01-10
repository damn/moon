(ns moon.world.tx.set-cooldown
  (:require [moon.entity.skills :as skills]))

(defn do!
  [{:keys [ctx/world]} eid skill]
  (swap! eid update :entity/skills skills/set-cooldown skill (:world/elapsed-time world))
  nil)
