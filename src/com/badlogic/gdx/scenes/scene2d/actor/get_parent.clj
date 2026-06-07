(ns com.badlogic.gdx.scenes.scene2d.actor.get-parent
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-parent [^Actor actor]
  (.getParent actor))
