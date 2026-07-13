(ns gdx.color
  (:require [com.badlogic.gdx.graphics.color :as color]))

(defn create [rgba]
  (color/new rgba))

(defn to-float-bits [rgba]
  (color/toFloatBits rgba))
