(ns moon.application.create.gdx-colors
  (:import (com.badlogic.gdx.graphics Color
                                      Colors)))

(defn step [ctx]
  (doseq [[name [r g b a]] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (Colors/put name (Color. r g b a)))
  ctx)
