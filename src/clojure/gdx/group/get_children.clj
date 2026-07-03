(ns clojure.gdx.group.get-children
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f [^Group group]
  (Group/.getChildren group))
