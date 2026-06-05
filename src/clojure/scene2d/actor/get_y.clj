(ns clojure.scene2d.actor.get-y
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-y [^Actor actor]
  (.getY actor))
