(ns clojure.gdx.input.get-y
  (:import (com.badlogic.gdx Input)))

(defn f [input]
  (Input/.getY input))
