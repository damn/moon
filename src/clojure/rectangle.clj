(ns clojure.rectangle
  (:refer-clojure :exclude [new contains?])
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]))

(defn new [& args]
  (apply rectangle/new args))

(defn overlaps? [& args]
  (apply rectangle/overlaps? args))

(defn contains? [& args]
  (apply rectangle/contains? args))
