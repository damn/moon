(ns input.position
  (:import (com.badlogic.gdx Input)))

(defn f [input]
  [(.getX input)
   (.getY input)])
