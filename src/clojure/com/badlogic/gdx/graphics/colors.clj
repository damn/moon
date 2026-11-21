(ns clojure.com.badlogic.gdx.graphics.colors
  (:import (com.badlogic.gdx.graphics Color Colors)))

(defn put [^String name ^Color color]
  (Colors/put name color))
