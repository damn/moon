(ns tx.show-message
  (:require [gdx.stage :as stage]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (.setUserObject (atom {:text message
                             :counter 0})))
  nil)
