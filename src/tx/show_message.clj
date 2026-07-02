(ns tx.show-message
  (:require [clojure.gdx.actor.set-user-object :as set-user-object])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(Group/.findActor % "player-message"))
      (set-user-object/f (atom {:text message
                                :counter 0})))
  nil)
