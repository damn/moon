(ns clojure.gdx.window.instance?
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [x]
  (instance? Window x))
