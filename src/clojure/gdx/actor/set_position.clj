(ns clojure.gdx.actor.set-position
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f
  ([^Actor actor x y]
   (Actor/.setPosition actor (float x) (float y)))
  ([^Actor actor x y align]
   (Actor/.setPosition actor (float x) (float y) align)))
