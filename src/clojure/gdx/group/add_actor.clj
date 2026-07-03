(ns clojure.gdx.group.add-actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f [^Group group ^Actor actor]
  (Group/.addActor group actor))
