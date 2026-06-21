(ns stage.windows
  (:require [clojure.actor.set-name :refer [set-name!]]
            [clojure.scenes.scene2d.group.create :refer [create-group]]
            [clojure.scenes.scene2d.group.add-actor :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
