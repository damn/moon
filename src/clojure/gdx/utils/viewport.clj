(ns clojure.gdx.utils.viewport
  (:require [badlogic.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn camera [^Viewport viewport]
  (.getCamera viewport))

(defn world-width [^Viewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^Viewport viewport]
  (.getWorldHeight viewport))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^Viewport viewport position]
  (-> viewport
      (.unproject (vector2/->java position))
      vector2/->clj))
