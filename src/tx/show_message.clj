(ns tx.show-message
  (:require [scene2d.actor.set-user-object :refer [set-user-object!]]
            [scene2d.group.find-actor :refer [find-actor]]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (find-actor "player-message")
      (set-user-object! (atom {:text message
                               :counter 0})))
  nil)
