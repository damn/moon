(ns clojure.gdx.actor.get-name
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getName actor))
