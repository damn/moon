(ns clojure.gdx.scene2d.actor.x
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-x [^Actor actor]
  (.getX actor))
