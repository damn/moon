(ns clojure.gdx.scene2d.actor.stage-local-coordinates
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn stage->local-coordinates [^Actor actor vector2]
  (.stageToLocalCoordinates actor vector2))
