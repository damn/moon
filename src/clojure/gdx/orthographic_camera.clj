(ns clojure.gdx.orthographic-camera
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [clj.api.com.badlogic.gdx.math.frustum :as frustum]
            [clj.api.com.badlogic.gdx.math.vector3 :as vector3]))

(def combined camera/combined)

(defn set-position! [camera [x y]]
  (vector3/set-x! (camera/position camera) x)
  (vector3/set-y! (camera/position camera) y)
  (camera/update! camera))

(defn set-zoom! [camera amount]
  (camera/set-zoom! camera amount)
  (camera/update! camera))

(defn zoom [camera]
  (camera/zoom camera))

(defn frustum [camera]
  (mapv vector3/->clj (frustum/plane-points (camera/frustum camera))))

(defn position [camera]
  (vector3/->clj (camera/position camera)))

(def viewport-width camera/viewport-width)
(def viewport-height camera/viewport-height)
