(ns clj.api.com.badlogic.gdx.graphics.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn float-bits
  "Calls `Color/toFloatBits`[link]."
  [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
