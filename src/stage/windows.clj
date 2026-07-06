(ns stage.windows
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn create [ctx actor-fns]
  (let [group (group/new)]
    (run! #(group/add-actor! group %) (for [f actor-fns] (f ctx)))
    (doto group
      (actor/set-name! "moon.ui.windows"))))
