(ns gdl.is-window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [actor]
  (instance? Window actor))
