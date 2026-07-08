(ns clojure.scene2d.actor.set-visible
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor visible?]
  (Actor/.setVisible actor visible?))
