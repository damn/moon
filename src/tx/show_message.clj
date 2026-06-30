(ns tx.show-message
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (gdx/find-actor "player-message")
      (gdx/set-user-object! (atom {:text message
                                   :counter 0})))
  nil)
