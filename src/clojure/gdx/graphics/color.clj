(ns clojure.gdx.graphics.color
  (:import (com.badlogic.gdx.graphics Color)))

(defn create [[r g b a]]
  (Color. r g b a))
