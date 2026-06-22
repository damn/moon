(ns gdl.find-actor
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn find-actor [^Group group name]
  (.findActor group name))
