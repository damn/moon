(ns clojure.rgba.float-bits
  (:import (com.badlogic.gdx.graphics Color)))

(defn f [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
