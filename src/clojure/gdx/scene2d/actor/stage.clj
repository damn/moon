(ns clojure.gdx.scene2d.actor.stage
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-stage [^Actor actor]
  (.getStage actor))
