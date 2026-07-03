(ns tx.show-message
  (:require [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.group.find-actor :as find-actor]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(find-actor/f % "player-message"))
      (set-user-object/f (atom {:text message
                                :counter 0})))
  nil)
