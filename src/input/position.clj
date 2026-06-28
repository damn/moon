(ns input.position
  (:import (com.badlogic.gdx Input)))

(defn f [input]
  [(.getX ^Input input)
   (.getY ^Input input)])
