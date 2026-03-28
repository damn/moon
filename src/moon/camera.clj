(ns moon.camera
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [clj.api.com.badlogic.gdx.math.frustum :as frustum]
            [clj.api.com.badlogic.gdx.math.vector3 :as vector3]))

(defn set-position! [camera [x y]]
  (vector3/set-x! (camera/position camera) x)
  (vector3/set-y! (camera/position camera) y)
  (camera/update! camera))

(defn set-zoom! [camera amount]
  (camera/set-zoom! camera amount)
  (camera/update! camera))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (camera/zoom cam) by))))

(defn frustum [camera]
  (let [plane-points (mapv vector3/->clj (frustum/plane-points (camera/frustum camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

(defn visible-tiles [camera]
  (let [[left-x right-x bottom-y top-y] (frustum camera)]
    (for [x (range (int left-x)   (int right-x))
          y (range (int bottom-y) (+ 2 (int top-y)))]
      [x y])))

(defn calculate-zoom
  "calculates the zoom value for camera to see all the 4 points."
  [camera & {:keys [left top right bottom]}]
  (let [viewport-width  (camera/viewport-width  camera)
        viewport-height (camera/viewport-height camera)
        [px py] (vector3/->clj (camera/position camera))
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
