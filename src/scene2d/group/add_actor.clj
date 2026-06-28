(ns scene2d.group.add-actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn add-actor! [^Group group ^Actor actor]
  (.addActor group actor))
