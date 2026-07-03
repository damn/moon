(ns clojure.gdx.input.get-x
  (:import (com.badlogic.gdx Input)))

(defn f [input]
  (Input/.getX input))
