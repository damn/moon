(ns clojure.gdx.actor.get-x
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getX actor))
