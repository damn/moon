(ns gdx.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn rgba->Color [[r g b a]]
  (Color. r g b a))
