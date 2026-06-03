(ns clojure.gdx.scene2d.actor.get-user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-user-object [^Actor actor]
  (.getUserObject actor))
