(ns moon.reaction-txs.show-message
  (:require [moon.ui.message :as message]))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      .getRoot
      (.findActor "player-message")
      (message/show! message))
  ctx)
