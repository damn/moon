(ns gdl.get-position
  (:import (com.badlogic.gdx Input)))

(defn f [^Input input]
  [(.getX input)
   (.getY input)])
