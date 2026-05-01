(ns clojure.graphics.viewport
  (:require [clojure.gdx.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn camera [^FitViewport viewport]
  (.getCamera viewport))

(defn world-width [^FitViewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^FitViewport viewport]
  (.getWorldHeight viewport))

(defn update! [^FitViewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^FitViewport viewport position]
  (-> viewport
      (.unproject (vector2/->java position))
      vector2/->clj))
