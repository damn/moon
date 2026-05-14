(ns clojure.graphics.orthographic-camera
  (:require [com.badlogic.gdx.math.vector3 :as vector3])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

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

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (zoom cam) by))))

(defn frustum [camera]
  (let [plane-points (frustum* camera)
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
