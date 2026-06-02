(ns clojure.gdx.scene2d.actor.stage-local-coordinates
  (:require [clojure.gdx.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn stage->local-coordinates [actor xy]
  (vector2/->clj (.stageToLocalCoordinates actor (vector2/create xy))))
