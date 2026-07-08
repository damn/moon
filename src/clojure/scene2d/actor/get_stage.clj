(ns clojure.scene2d.actor.get-stage
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [actor]
  (Actor/.getStage actor))
