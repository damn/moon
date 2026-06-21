(ns clojure.actor.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-stage [^Actor actor]
  (.getStage actor))
