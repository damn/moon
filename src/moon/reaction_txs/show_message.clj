(ns moon.reaction-txs.show-message
  (:require [clojure.scene2d.actor :as actor]
            [moon.stage :as stage]))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (actor/set-user-object! (atom {:text message
                                     :counter 0})))
  ctx)
