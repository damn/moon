(ns clojure.gdx.math.vector2
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]))

(defn new [[x y]]
  (vector2/new x y))

(defn clojurize [v2]
  [(vector2/x v2) (vector2/y v2)])
