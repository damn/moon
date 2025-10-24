(ns clojure.gdx.graphics.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn create [[r g b a]]
  (Color. r g b a))

(defn float-bits
  "Packs the color components into a 32-bit integer with the format ABGR and then converts it to a float.

  Returns: the packed color as a 32-bit float"
  [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
