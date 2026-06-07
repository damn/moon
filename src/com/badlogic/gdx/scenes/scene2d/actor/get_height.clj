(ns com.badlogic.gdx.scenes.scene2d.actor.get-height
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-height [^Actor actor]
  (.getHeight actor))
