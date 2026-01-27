(ns moon.reaction-txs.show-message
  (:require [moon.ui.message :as message])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      Stage/.getRoot
      (.findActor "player-message")
      (message/show! message))
  ctx)
