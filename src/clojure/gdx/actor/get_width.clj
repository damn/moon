(ns clojure.gdx.actor.get-width
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getWidth actor))
