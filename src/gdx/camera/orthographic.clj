(ns gdx.camera.orthographic
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [com.badlogic.gdx.math.frustum :as frustum]
            [gdx.math.vector3 :as vector3]))

(defn new []
  (camera/new))

(defn combined [orthographic-camera]
  (camera/combined orthographic-camera))

(defn set-to-ortho! [orthographic-camera y-down viewport-width viewport-height]
  (camera/setToOrtho orthographic-camera y-down viewport-width viewport-height))

(defn zoom [orthographic-camera]
  (camera/zoom orthographic-camera))

(defn viewport-width [orthographic-camera]
  (camera/viewportWidth orthographic-camera))

(defn viewport-height [orthographic-camera]
  (camera/viewportHeight orthographic-camera))

(defn up [orthographic-camera]
  (camera/up orthographic-camera))

(defn position-vec3 [orthographic-camera]
  (camera/position orthographic-camera))

(defn set-zoom! [orthographic-camera amount]
  (camera/set-zoom! orthographic-camera amount)
  (camera/update orthographic-camera))

(defn inc-zoom! [orthographic-camera by]
  (set-zoom! orthographic-camera (max 0.1 (+ (zoom orthographic-camera) by))))

(defn position [orthographic-camera]
  (vector3/clojurize (position-vec3 orthographic-camera)))

(defn set-position! [orthographic-camera [x y]]
  (let [pos (position-vec3 orthographic-camera)]
    (vector3/set-x! pos x)
    (vector3/set-y! pos y))
  (camera/update orthographic-camera))

(defn frustum [orthographic-camera]
  (let [plane-points (mapv vector3/clojurize (frustum/planePoints (camera/frustum orthographic-camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

(defn visible-tiles [orthographic-camera]
  (let [[left-x right-x bottom-y top-y] (frustum orthographic-camera)]
    (for [x (range (int left-x)   (int right-x))
          y (range (int bottom-y) (+ 2 (int top-y)))]
      [x y])))

(defn calculate-zoom
  "calculates the zoom value for camera to see all the 4 points."
  [orthographic-camera {:keys [left top right bottom]}]
  (let [viewport-width  (viewport-width orthographic-camera)
        viewport-height (viewport-height orthographic-camera)
        [px py] (position orthographic-camera)
        px (float px)
        py (float py)
        leftx (float (left 0))
        rightx (float (right 0))
        x-diff (max (- px leftx) (- rightx px))
        topy (float (top 1))
        bottomy (float (bottom 1))
        y-diff (max (- topy py) (- py bottomy))
        vp-ratio-w (/ (* x-diff 2) viewport-width)
        vp-ratio-h (/ (* y-diff 2) viewport-height)]
    (max vp-ratio-w vp-ratio-h)))

(defn zoom-to-rect [orthographic-camera rectangle]
  (set-zoom! orthographic-camera
             (calculate-zoom orthographic-camera rectangle)))
