(ns com.badlogic.gdx.scenes.scene2d.ui
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn window? [actor]
  (instance? Window actor))
