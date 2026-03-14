(ns moon.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn float-bits
  ^Float
  [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
