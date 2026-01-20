(ns moon.reaction-txs.show-message
  (:require [moon.stage :as stage]
            [moon.ui.group :as group]
            [moon.ui.message :as message]))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      stage/root
      (group/find-actor "player-message")
      (message/show! message))
  ctx)
