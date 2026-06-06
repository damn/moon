(ns gdx.scene2d.group.clear-children
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn clear-children! [^Group group]
  (.clearChildren group))
