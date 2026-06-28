(ns scene2d.actor.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor]
  (.getStage actor))
