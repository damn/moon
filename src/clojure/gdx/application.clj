(ns clojure.gdx.application
  (:require [com.badlogic.gdx.application :as application]))

(defn get-audio [app]
  (application/getAudio app))

(defn get-files [app]
  (application/getFiles app))

(defn get-graphics [app]
  (application/getGraphics app))

(defn get-input [app]
  (application/getInput app))

(defn post-runnable! [app f]
  (application/postRunnable app f))
