(ns com.badlogic.gdx.graphics.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create [{:keys [y-down? world-width world-height]}]
  (doto (OrthographicCamera.)
    (.setToOrtho y-down? world-width world-height)))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportWidth camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn update! [^OrthographicCamera camera]
  (.update camera))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))
