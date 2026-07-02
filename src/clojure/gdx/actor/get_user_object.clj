(ns clojure.gdx.actor.get-user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getUserObject actor))
