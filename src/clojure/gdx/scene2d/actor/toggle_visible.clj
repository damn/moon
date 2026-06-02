(ns clojure.gdx.scene2d.actor.toggle-visible
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn toggle-visible! [^Actor actor]
  (.setVisible actor (not (.isVisible actor))))
