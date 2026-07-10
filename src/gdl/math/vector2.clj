(ns gdl.math.vector2
  (:require [com.badlogic.gdx.math.vector2 :as vector2]))

(defn clojurize [v2]
  [(vector2/x v2) (vector2/y v2)])
