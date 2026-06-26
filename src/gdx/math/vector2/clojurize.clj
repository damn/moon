(ns gdx.math.vector2.clojurize
  (:require [com.badlogic.gdx.math.vector2 :as vector2]))

(defn f [v2]
  [(vector2/x v2)
   (vector2/y v2)])
