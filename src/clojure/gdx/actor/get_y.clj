(ns clojure.gdx.actor.get-y
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getY actor))
