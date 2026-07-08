(ns clojure.actor.set-touchable
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor touchable]
  (Actor/.setTouchable actor touchable))
