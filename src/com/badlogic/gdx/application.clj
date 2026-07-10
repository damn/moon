(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn getAudio [application]
  (.getAudio ^Application application))

(defn getFiles [application]
  (.getFiles ^Application application))

(defn getGraphics [application]
  (.getGraphics ^Application application))

(defn getInput [application]
  (.getInput ^Application application))

(defn postRunnable [application f]
  (.postRunnable ^Application application f))
