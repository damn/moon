(ns clojure.com.badlogic.gdx.graphics.color
  (:import (com.badlogic.gdx.graphics Color)))

(def white Color/WHITE)

(defn create [r g b a]
  (Color. (float r) (float g) (float b) (float a)))

(defn float-bits
  ([^Color color]
   (.toFloatBits color))
  ([r g b a]
   (Color/toFloatBits (float r) (float g) (float b) (float a))))
