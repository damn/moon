(ns clojure.gdx.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-name! [^Actor actor name]
  (.setName actor name))
