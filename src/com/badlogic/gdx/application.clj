(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn input [^Application app]
  (.getInput app))
