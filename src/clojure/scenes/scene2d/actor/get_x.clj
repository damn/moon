(ns clojure.scenes.scene2d.actor.get-x
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-x [^Actor actor]
  (.getX actor))
