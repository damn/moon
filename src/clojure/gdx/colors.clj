(ns clojure.gdx.colors
  (:import (com.badlogic.gdx.graphics Color
                                      Colors)))

(defn put! [colors]
  (doseq [[name [r g b a]] colors]
    (Colors/put name (Color. r g b a))))
