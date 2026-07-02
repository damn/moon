(ns clojure.gdx.actor.visible?
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.isVisible actor))
