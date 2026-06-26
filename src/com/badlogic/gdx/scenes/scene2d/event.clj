(ns com.badlogic.gdx.scenes.scene2d.event
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn get-stage [^Event event]
  (.getStage event))
