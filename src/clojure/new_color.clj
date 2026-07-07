(ns clojure.new-color
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Color)))

(defn f [[r g b a]]
  (Color. r g b a))
