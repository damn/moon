(ns gdl.vector2.clojurize
  (:import (com.badlogic.gdx.math Vector2)))

(defn f [^Vector2 v2]
  [(.x v2)
   (.y v2)])
