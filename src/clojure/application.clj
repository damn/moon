(ns clojure.application
  (:import (com.badlogic.gdx Application)))

(defn get-audio [application]
  (Application/.getAudio application))

(defn get-files [application]
  (Application/.getFiles application))

(defn get-graphics [application]
  (Application/.getGraphics application))

(defn get-input [application]
  (Application/.getInput application))

(defn post-runnable! [application f]
  (Application/.postRunnable application f))
