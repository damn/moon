(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn audio [^Application app]
  (.getAudio app))

(defn files [^Application app]
  (.getFiles app))

(defn graphics [^Application app]
  (.getGraphics app))

(defn input [^Application app]
  (.getInput app))
