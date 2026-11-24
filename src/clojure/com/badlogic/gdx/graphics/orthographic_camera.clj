(ns clojure.com.badlogic.gdx.graphics.orthographic-camera
  "A camera with orthographic projection."
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create []
  (OrthographicCamera.))

(defn set-to-ortho!
  "Sets this camera to an orthographic projection, centered at (viewportWidth/2, viewportHeight/2), with the y-axis pointing
  up or down.
  
  - `yDown`: whether y should be pointing down.
  
  - `viewportWidth`
  
  - `viewportHeight`"
  [^OrthographicCamera camera y-down? viewport-width viewport-height]
  (.setToOrtho camera y-down? (float viewport-width) (float viewport-height)))

(defn zoom
  "The zoom of the camera"
  [^OrthographicCamera camera]
  (.zoom camera))

(defn set-zoom!
  "The zoom of the camera"
  [^OrthographicCamera camera zoom]
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

(defn update!
  "Recalculates the projection and view matrix of this camera and the [[clojure.com.badlogic.gdx.math.frustum]] planes. Use this after you've
  manipulated any of the attributes of the camera."
  [^OrthographicCamera camera]
  (.update camera))
