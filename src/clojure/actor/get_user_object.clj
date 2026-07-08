(ns clojure.actor.get-user-object
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (Actor/.getUserObject actor))
