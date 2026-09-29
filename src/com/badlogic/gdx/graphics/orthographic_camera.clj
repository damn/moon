(ns com.badlogic.gdx.graphics.orthographic-camera
  (:refer-clojure :exclude [new update])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn new []
  (OrthographicCamera.))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn setToOrtho [^OrthographicCamera camera y-down viewport-width viewport-height]
  (.setToOrtho camera y-down viewport-width viewport-height))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))

(defn up [^OrthographicCamera camera]
  (.up camera))

(defn update [^OrthographicCamera camera]
  (.update camera))

(defn viewportHeight [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn viewportWidth [^OrthographicCamera camera]
  (.viewportWidth camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))
