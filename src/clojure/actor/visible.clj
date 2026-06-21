(ns clojure.actor.visible
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn visible? [^Actor actor]
  (.isVisible actor))
