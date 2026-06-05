(ns clojure.scene2d.actor.set-user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-user-object! [^Actor actor user-object]
  (.setUserObject actor user-object))
