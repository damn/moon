(ns tx.show-message
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(Group/.findActor % "player-message"))
      (Actor/.setUserObject (atom {:text message
                                   :counter 0})))
  nil)
