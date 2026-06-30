(ns tx.show-message
  (:require [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (gdx/find-actor "player-message")
      (Actor/.setUserObject (atom {:text message
                                   :counter 0})))
  nil)
