(ns gdx.viewport
  (:require [gdx.to-clj :refer [->clj]])
  (:import (com.badlogic.gdx.utils.viewport Viewport)
           (com.badlogic.gdx.math Vector2)))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^Viewport viewport [x y]]
  (-> viewport
      (.unproject (Vector2. x y))
      ->clj))
