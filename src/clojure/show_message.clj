(ns clojure.show-message
  (:require [clojure.group :as group]
            [clojure.actor :as actor]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(group/find-actor % "player-message"))
      (actor/set-user-object! (atom {:text message
                                :counter 0})))
  nil)
