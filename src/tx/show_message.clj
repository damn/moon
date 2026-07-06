(ns tx.show-message
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.find-actor :as find-actor]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(find-actor/f % "player-message"))
      (actor/set-user-object! (atom {:text message
                                :counter 0})))
  nil)
