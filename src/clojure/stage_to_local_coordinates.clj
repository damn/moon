(ns clojure.stage-to-local-coordinates
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor ^Vector2 screen-coords]
  (Actor/.stageToLocalCoordinates actor screen-coords))
