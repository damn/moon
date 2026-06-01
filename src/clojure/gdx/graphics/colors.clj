(ns clojure.gdx.graphics.colors
  (:import (com.badlogic.gdx.graphics Colors)))

(defn put! [name color]
  (Colors/put name color))
