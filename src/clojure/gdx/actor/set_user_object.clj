(ns clojure.gdx.actor.set-user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor user-object]
  (Actor/.setUserObject actor user-object))
