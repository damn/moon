(ns clojure.gdx.scene2d.actor.height
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-height [^Actor actor]
  (.getHeight actor))

