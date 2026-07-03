(ns clojure.gdx.event.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn f [^Event event]
  (Event/.getStage event))
