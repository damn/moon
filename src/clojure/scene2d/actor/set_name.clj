(ns clojure.scene2d.actor.set-name
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-name! [^Actor actor name]
  (.setName actor name))
