(ns clojure.gdx.scene2d.actor.parent
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-parent [^Actor actor]
  (.getParent actor))
