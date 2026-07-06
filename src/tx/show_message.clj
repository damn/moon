(ns tx.show-message
  (:require
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(group/find-actor % "player-message"))
      (actor/set-user-object! (atom {:text message
                                :counter 0})))
  nil)
