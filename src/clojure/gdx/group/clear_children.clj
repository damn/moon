(ns clojure.gdx.group.clear-children
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f [^Group group]
  (Group/.clearChildren group))
