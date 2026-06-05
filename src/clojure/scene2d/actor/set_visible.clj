(ns clojure.scene2d.actor.set-visible
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))
