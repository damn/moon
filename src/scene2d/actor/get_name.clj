(ns scene2d.actor.get-name
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-name [^Actor actor]
  (.getName actor))
