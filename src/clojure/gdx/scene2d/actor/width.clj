(ns clojure.gdx.scene2d.actor.width
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-width [^Actor actor]
  (.getWidth actor))
