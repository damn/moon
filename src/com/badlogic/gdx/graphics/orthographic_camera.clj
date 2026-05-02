(ns com.badlogic.gdx.graphics.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho! [^OrthographicCamera camera y-down? world-width world-height]
  (.setToOrtho camera y-down? world-width world-height))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn update! [^OrthographicCamera camera]
  (.update camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))
