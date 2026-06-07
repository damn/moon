(ns stage.windows
  (:require [com.badlogic.gdx.scenes.scene2d.actor.set-name :refer [set-name!]]
            [com.badlogic.gdx.scenes.scene2d.group.create :refer [create-group]]
            [com.badlogic.gdx.scenes.scene2d.group.add-actor :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
