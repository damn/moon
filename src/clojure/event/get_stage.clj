(ns clojure.event.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn get-stage [^Event event]
  (.getStage event))
