(ns clojure.gdx.actor.remove
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.remove actor))
