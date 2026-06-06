(ns stage.windows
  (:require [gdx.scene2d.actor.set-name :refer [set-name!]]
            [gdx.scene2d.group.create :refer [create-group]]
            [gdx.scene2d.group.add-actor :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
