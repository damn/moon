(ns clj.api.com.badlogic.gdx.graphics.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn update! [^OrthographicCamera camera]
  (.update camera))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))

(defn set-to-ortho! [^OrthographicCamera camera y-down? viewport-width viewport-height]
  (.setToOrtho camera y-down? viewport-width viewport-height))
