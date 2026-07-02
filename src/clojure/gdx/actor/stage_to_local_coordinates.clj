(ns clojure.gdx.actor.stage-to-local-coordinates
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor ^Vector2 screen-coords]
  (Actor/.stageToLocalCoordinates actor screen-coords))
