(ns orthographic-camera.calculate-zoom
  (:require [clojure.gdx :as gdx]
            [orthographic-camera.position :as get-position]))

(defn calculate-zoom
  "calculates the zoom value for camera to see all the 4 points."
  [camera {:keys [left top right bottom]}]
  (let [viewport-width  (gdx/camera-viewport-width camera)
        viewport-height (gdx/camera-viewport-height camera)
        [px py] (get-position/f camera)
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
