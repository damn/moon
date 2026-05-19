(ns com.badlogic.gdx.graphics.orthographic-camera
  (:require [com.badlogic.gdx.math.vector3 :as vector3]
            [gdl.graphics.orthographic-camera :as camera])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create [{:keys [y-down? world-width world-height]}]
  (doto (OrthographicCamera.)
    (.setToOrtho y-down? world-width world-height)))

(extend-type OrthographicCamera
  gdl.graphics.orthographic-camera/OrthographicCamera
  (combined [camera]
    (.combined camera))

  (zoom [camera]
    (.zoom camera))

  (frustum [camera]
    (let [plane-points (mapv vector3/->clj (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          right-x  (apply max (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))
          top-y    (apply max (map second frustum-points))]
      [left-x right-x bottom-y top-y]))

  (position [camera]
    (vector3/->clj (.position camera)))

  (set-position! [camera [x y]]
    (set! (.x (.position camera)) x)
    (set! (.y (.position camera)) y)
    (.update camera))

  (set-zoom! [camera amount]
    (set! (.zoom camera) amount)
    (.update camera))

  (inc-zoom! [cam by]
    (camera/set-zoom! cam (max 0.1 (+ (camera/zoom cam) by))))

  (visible-tiles [camera]
    (let [[left-x right-x bottom-y top-y] (camera/frustum camera)]
      (for [x (range (int left-x)   (int right-x))
            y (range (int bottom-y) (+ 2 (int top-y)))]
        [x y])))

  (calculate-zoom [camera {:keys [left top right bottom]}]
    (let [viewport-width  (.viewportWidth  camera)
          viewport-height (.viewportHeight camera)
          [px py] (camera/position camera)
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
      new-zoom)))
