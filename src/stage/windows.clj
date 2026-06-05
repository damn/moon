(ns stage.windows
  (:require [clojure.scene2d.actor.set-name :refer [set-name!]]
            [clojure.scene2d.group.create :refer [create-group]]
            [clojure.scene2d.group.add-actor :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
