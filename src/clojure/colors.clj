(ns clojure.colors
  (:import (com.badlogic.gdx.graphics Colors)))

(defn put! [name color]
  (Colors/put name color))
