(ns clojure.show-message
  (:require
            [clojure.set-user-object] [clojure.group :as group]))

(defn f
  [{:keys [ctx/stage] :as ctx} message]
  (-> stage
      :stage/root
      (#(group/find-actor % "player-message"))
      (clojure.set-user-object/f (atom {:text message
                                :counter 0})))
  nil)
