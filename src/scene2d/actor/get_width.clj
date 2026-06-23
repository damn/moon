(ns scene2d.actor.get-width
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-width [^Actor actor]
  (.getWidth actor))
