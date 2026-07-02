(ns clojure.gdx.actor.get-parent
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getParent actor))
