(ns clojure.gdx.utils.viewport
  (:require [clojure.gdx.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^Viewport viewport xy]
  (-> viewport
      (.unproject (vector2/create xy))
      vector2/->clj))
