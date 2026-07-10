(ns com.badlogic.gdx.scenes.scene2d.event
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn getStage [^Event event]
  (.getStage event))
