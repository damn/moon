(ns clojure.actor.get-name
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-name [^Actor actor]
  (.getName actor))
