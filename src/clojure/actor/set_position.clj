(ns clojure.actor.set-position
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-position!
  ([^Actor actor [x y]]
   (.setPosition actor x y))
  ([^Actor actor [x y] align]
   (.setPosition actor x y align)))
