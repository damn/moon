(ns clojure.gdx.scene2d.actor.set-user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-user-object! [^Actor actor object]
  (.setUserObject actor object))
