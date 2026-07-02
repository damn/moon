(ns clojure.gdx.actor.set-name
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor name]
  (Actor/.setName actor name))
