(ns clojure.application
  (:require [com.badlogic.gdx.application :as application]))

(defn get-audio [application]
  (application/get-audio application))

(defn get-files [application]
  (application/get-files application))

(defn get-graphics [application]
  (application/get-graphics application))

(defn get-input [application]
  (application/get-input application))

(defn post-runnable! [application f]
  (application/post-runnable! application f))
