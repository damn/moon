(ns gdl.graphics.orthographic-camera
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]))

(defn combined [& args]
  (apply orthographic-camera/combined args))

(defn frustum [& args]
  (apply orthographic-camera/frustum args))

(defn new [& args]
  (apply orthographic-camera/new args))

(defn position [& args]
  (apply orthographic-camera/position args))

(defn set-to-ortho! [& args]
  (apply orthographic-camera/setToOrtho args))

(defn set-zoom! [& args]
  (apply orthographic-camera/set-zoom! args))

(defn up [& args]
  (apply orthographic-camera/up args))

(defn update! [& args]
  (apply orthographic-camera/update args))

(defn viewport-height [& args]
  (apply orthographic-camera/viewportHeight args))

(defn viewport-width [& args]
  (apply orthographic-camera/viewportWidth args))

(defn zoom [& args]
  (apply orthographic-camera/zoom args))
