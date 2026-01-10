(ns moon.ui.event
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn stage [^Event event]
  (.getStage event))
