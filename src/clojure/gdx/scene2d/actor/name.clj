(ns clojure.gdx.scene2d.actor.name
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-name [^Actor actor]
  (.getName actor))
