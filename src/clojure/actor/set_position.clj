(ns clojure.actor.set-position
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f
  ([^Actor actor x y]
   (Actor/.setPosition actor (float x) (float y)))
  ([^Actor actor x y align]
   (Actor/.setPosition actor (float x) (float y) align)))
