(ns clj.api.com.badlogic.gdx.graphics.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn float-bits
  "Packs the color components into a 32-bit integer with the format ABGR and then converts it to a float.

  Returns: the packed color as a 32-bit float"
  [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
