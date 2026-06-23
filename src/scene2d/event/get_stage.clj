(ns scene2d.event.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn f [^Event event]
  (.getStage event))
