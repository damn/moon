(ns clojure.vector2
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]))

(defn new [& args]
  (apply vector2/new args))

(defn x [& args]
  (apply vector2/x args))

(defn y [& args]
  (apply vector2/y args))

(defn clojurize [v2]
  [(x v2) (y v2)])
