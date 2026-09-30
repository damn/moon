(ns gdx.event
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn get-stage [e]
  (.getStage ^Event e))
