(ns com.badlogic.gdx.graphics.orthographic-camera
  (:refer-clojure :exclude [new update])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn combined [^OrthographicCamera camera]
  (.combined camera))

(defn frustum [^OrthographicCamera camera]
  (.frustum camera))

(defn new []
  (OrthographicCamera.))

(defn position [^OrthographicCamera camera]
  (.position camera))

; TODO exclamation marks rempve
; TODO Also original names - setToOrtho ?
; itsthe clojure API ! -> Constructors are (OrthographicCamera foo bar baz ) ???
; maybe evernytihng in one namespace?
(defn set-to-ortho! [^OrthographicCamera camera y-down viewport-width viewport-height]
  (.setToOrtho camera y-down viewport-width viewport-height))

(defn set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))

(defn up [^OrthographicCamera camera]
  (.up camera))

(defn update! [^OrthographicCamera camera]
  (.update camera))

(defn viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))

(defn viewport-width [^OrthographicCamera camera]
  (.viewportWidth camera))

(defn zoom [^OrthographicCamera camera]
  (.zoom camera))
