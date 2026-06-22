(ns gdl.get-user-object
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn get-user-object [^Actor actor]
  (.getUserObject actor))
