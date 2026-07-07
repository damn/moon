(ns clojure.event
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn get-stage [^Event event]
  (Event/.getStage event))
