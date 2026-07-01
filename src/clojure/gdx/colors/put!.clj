(ns clojure.gdx.colors.put!
  (:import (com.badlogic.gdx.graphics Colors)))

(defn f [name color]
  (Colors/put name color))
