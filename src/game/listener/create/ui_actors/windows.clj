(ns game.listener.create.ui-actors.windows
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn windows-create [actor-fns]
  (let [group* (Group.)]
    (run! #(.addActor ^Group group* ^Actor %) (for [f actor-fns] (f)))
    (doto group*
      (.setName "moon.ui.windows"))))
