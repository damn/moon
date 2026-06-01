(ns reaction-txs.show-message
  (:require [gdx.scenes.scene2d.actor :as actor]
            [gdx.stage :as stage]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (actor/set-user-object! (atom {:text message
                                     :counter 0})))
  ctx)
