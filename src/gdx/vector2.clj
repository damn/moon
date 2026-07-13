(ns gdx.vector2
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.vector2 :as vector2]))

(defn new [[x y]]
  (vector2/new x y))

(defn clojurize [v2]
  [(vector2/x v2) (vector2/y v2)])
