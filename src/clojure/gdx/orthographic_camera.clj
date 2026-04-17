(ns clojure.gdx.orthographic-camera
  (:require [clj.api.com.badlogic.gdx.math.vector3 :as vector3])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho! [^OrthographicCamera camera y-down? viewport-width viewport-height]
  (.setToOrtho camera y-down? viewport-width viewport-height))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn set-position! [^OrthographicCamera camera [x y]]
  (set! (.x (.position camera)) x)
  (set! (.y (.position camera)) y)
  (.update camera))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount)
  (.update camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn frustum* [^OrthographicCamera camera]
  (mapv vector3/->clj (.planePoints (.frustum camera))))

(defn position [^OrthographicCamera camera]
  (vector3/->clj (.position camera)))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))
