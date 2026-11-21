(ns clojure.com.badlogic.gdx.graphics.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho! [^OrthographicCamera camera y-down? viewport-width viewport-height]
  (.setToOrtho camera y-down? (float viewport-width) (float viewport-height)))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn set-zoom! [^OrthographicCamera camera zoom]
  (set! (.zoom camera) zoom))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportWidth camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn up [^OrthographicCamera camera]
  (.up camera))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn update! [^OrthographicCamera camera]
  (.update camera))
