(ns moon.reaction-txs.show-message
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      Stage/.getRoot
      (.findActor "player-message")
      (.setUserObject (atom {:text message
                             :counter 0})))
  ctx)
