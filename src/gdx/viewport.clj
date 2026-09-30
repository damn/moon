(ns gdx.viewport
  (:require [gdx.vector2 :as vector2])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn get-world-width [viewport]
  (.getWorldWidth ^Viewport viewport))

(defn get-world-height [viewport]
  (.getWorldHeight ^Viewport viewport))

(defn get-camera [viewport]
  (.getCamera ^Viewport viewport))

(defn update! [viewport screen-width screen-height center-camera?]
  (.update ^Viewport viewport screen-width screen-height center-camera?))

(defn unproject [viewport v2]
  (vector2/clojurize (.unproject ^Viewport viewport ^Vector2 (vector2/new v2))))