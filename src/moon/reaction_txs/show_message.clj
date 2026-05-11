(ns moon.reaction-txs.show-message
  (:require [moon.stage :as stage]
            [moon.ui.actor :as actor]))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (actor/set-user-object! (atom {:text message
                                     :counter 0})))
  ctx)
