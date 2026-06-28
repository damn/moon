(ns gl20.clear-color
  (:import (com.badlogic.gdx.graphics GL20)))

(defn f [gl r g b a]
  (.glClearColor ^GL20 gl r g b a))
