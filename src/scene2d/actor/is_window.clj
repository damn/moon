(ns scene2d.actor.is-window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [actor]
  (instance? Window actor))
