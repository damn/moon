(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn graphics [^Application app]
  (.getGraphics app))

(defn input [^Application app]
  (.getInput app))
