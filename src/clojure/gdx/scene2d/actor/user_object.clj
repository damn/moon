(ns clojure.gdx.scene2d.actor.user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-user-object [^Actor actor]
  (.getUserObject actor))
