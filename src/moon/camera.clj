(ns moon.camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.math Vector3)))

(defn set-zoom! [^OrthographicCamera orthographic-camera amount]
  (set! (.zoom orthographic-camera) amount)
  (.update orthographic-camera))

(defn inc-zoom! [^OrthographicCamera orthographic-camera by]
  (set-zoom! orthographic-camera (max 0.1 (+ (.zoom orthographic-camera) by))))

(defn position [^OrthographicCamera orthographic-camera]
  (let [v3 (.position orthographic-camera)]
    [(.x v3) (.y v3) (.z v3)]))

(defn set-position! [^OrthographicCamera orthographic-camera [x y]]
  (let [pos (.position orthographic-camera)]
    (set! (.x pos) x)
    (set! (.y pos) y))
  (.update orthographic-camera))

(defn frustum [^OrthographicCamera orthographic-camera]
  (let [plane-points (mapv (fn [^Vector3 v3]
                             [(.x v3) (.y v3) (.z v3)])
                           (.planePoints (.frustum orthographic-camera)))
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
  [^OrthographicCamera orthographic-camera {:keys [left top right bottom]}]
  (let [viewport-width  (.viewportWidth orthographic-camera)
        viewport-height (.viewportHeight orthographic-camera)
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
