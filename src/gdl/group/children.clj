(ns gdl.group.children
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn children [^Group group]
  (.getChildren group))
