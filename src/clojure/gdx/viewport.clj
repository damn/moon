(ns clojure.gdx.viewport
  (:require [clojure.gdx.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^Viewport viewport position]
  (-> viewport
      (.unproject (vector2/->java position))
      vector2/->clj))
