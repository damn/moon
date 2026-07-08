(ns clojure.actor.get-x
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getX actor))
