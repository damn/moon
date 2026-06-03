(ns tx.show-message
  (:require [clojure.gdx.scene2d.actor :refer [set-user-object!]]
            [clojure.gdx.scene2d.group.find-actor :refer [find-actor]]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (find-actor "player-message")
      (set-user-object! (atom {:text message
                               :counter 0})))
  nil)
