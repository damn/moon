(ns clojure.scene2d.event
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn stage [event]
  (.getStage event))
