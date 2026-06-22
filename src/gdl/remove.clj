(ns gdl.remove
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn remove! [^Actor actor]
  (.remove actor))
