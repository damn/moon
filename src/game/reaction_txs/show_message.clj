(ns game.reaction-txs.show-message
  (:require [clojure.scene2d.stage :as stage]
            [clojure.scene2d.actor :as actor]))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (actor/set-user-object! (atom {:text message
                                     :counter 0})))
  ctx)
