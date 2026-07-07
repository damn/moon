(ns com.badlogic.gdx.graphics.color
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Color)))

(defn new [[r g b a]]
  (Color. r g b a))

(defn float-bits [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))
