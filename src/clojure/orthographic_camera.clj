(ns clojure.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho! [^OrthographicCamera camera y-down? world-width world-height]
  (.setToOrtho camera y-down? world-width world-height))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportWidth camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))

(defn update! [^OrthographicCamera camera]
  (.update camera))
