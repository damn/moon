(ns clojure.gdx.graphics.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho! [camera y-down? world-width world-height]
  (OrthographicCamera/.setToOrtho camera y-down? world-width world-height))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportWidth camera))

(defn position [^OrthographicCamera camera]
  (.position camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn set-position! [^OrthographicCamera camera [x y]]
  (set! (.x (.position camera)) (float x))
  (set! (.y (.position camera)) (float y)))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn update! [^OrthographicCamera camera]
  (.update camera))
