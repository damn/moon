(ns tx.show-message
  (:require [clojure.gdx.scene2d.actor :refer [set-user-object!]]
            [gdx.stage :as stage]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      (stage/find-actor "player-message")
      (set-user-object! (atom {:text message
                               :counter 0})))
  nil)
