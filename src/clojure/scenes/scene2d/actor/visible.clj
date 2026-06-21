(ns clojure.scenes.scene2d.actor.visible
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn visible? [^Actor actor]
  (.isVisible actor))
