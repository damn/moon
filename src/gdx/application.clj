(ns gdx.application
  (:import (com.badlogic.gdx Application)))

(defn get-audio [app]
  (.getAudio ^Application app))

(defn get-files [app]
  (.getFiles ^Application app))

(defn get-graphics [app]
  (.getGraphics ^Application app))

(defn get-input [app]
  (.getInput ^Application app))

(defn post-runnable! [app f]
  (.postRunnable ^Application app f))
