(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn files [^Application app]
  (.getFiles app))

(defn graphics [^Application app]
  (.getGraphics app))

(defn input [^Application app]
  (.getInput app))
