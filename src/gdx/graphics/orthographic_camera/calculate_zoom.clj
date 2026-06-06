(ns gdx.graphics.orthographic-camera.calculate-zoom
  (:require [gdx.graphics.orthographic-camera.get-position :refer [get-position]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn calculate-zoom
  "calculates the zoom value for camera to see all the 4 points."
  [^OrthographicCamera camera {:keys [left top right bottom]}]
  (let [viewport-width  (.viewportWidth  camera)
        viewport-height (.viewportHeight camera)
        [px py] (get-position camera)
        px (float px)
        py (float py)
        leftx (float (left 0))
        rightx (float (right 0))
        x-diff (max (- px leftx) (- rightx px))
        topy (float (top 1))
        bottomy (float (bottom 1))
        y-diff (max (- topy py) (- py bottomy))
        vp-ratio-w (/ (* x-diff 2) viewport-width)
        vp-ratio-h (/ (* y-diff 2) viewport-height)
        new-zoom (max vp-ratio-w vp-ratio-h)]
    new-zoom))
