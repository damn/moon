(ns moon.impl.ui-viewport
  (:require [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.viewport :as viewport]))

(defn create [_ctx {:keys [width height]}]
  (viewport/create width height (orthographic-camera/create)))
