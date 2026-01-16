(ns moon.create.colors
  (:import (com.badlogic.gdx.graphics Color
                                      Colors)))

(defn step [ctx colors]
  (doseq [[name [r g b a]] colors]
    (Colors/put name (Color. r g b a)))
  ctx)
