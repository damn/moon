(ns clojure.scene2d.actor.add-listener
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [actor listener]
  (Actor/.addListener actor listener))
