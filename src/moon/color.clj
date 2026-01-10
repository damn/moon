(ns moon.color
  (:import (com.badlogic.gdx.graphics Color)))

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(defn float-bits
  "Packs the color components into a 32-bit integer with the format ABGR and then converts it to a float.

  Returns: the packed color as a 32-bit float"
  [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
