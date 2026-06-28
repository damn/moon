(ns stage.windows
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn create [ctx actor-fns]
  (let [group (Group.)]
    (run! #(Group/.addActor group %) (for [f actor-fns] (f ctx)))
    (doto group
      (Actor/.setName "moon.ui.windows"))))
