(ns stage.windows
  (:require [clojure.actor.set-name :refer [set-name!]]
            [clojure.group.create :refer [create-group]]
            [clojure.group.add-actor :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
