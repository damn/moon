(ns gdx.graphics.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn create [[r g b a]]
  (Color. r g b a))

(defn float-bits [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
