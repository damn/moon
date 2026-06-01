(ns reaction-txs.show-message
  (:require [gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [gdx.stage :as stage]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (set-user-object! (atom {:text message
                               :counter 0})))
  ctx)
