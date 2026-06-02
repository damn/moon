(ns clojure.gdx.scene2d.actor.stage-local-coordinates
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn stage->local-coordinates [actor vector2]
  (Actor/.stageToLocalCoordinates actor vector2))
