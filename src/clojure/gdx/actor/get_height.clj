(ns clojure.gdx.actor.get-height
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getHeight actor))
