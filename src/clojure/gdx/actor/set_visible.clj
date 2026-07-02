(ns clojure.gdx.actor.set-visible
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor visible?]
  (Actor/.setVisible actor visible?))
