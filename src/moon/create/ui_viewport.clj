(ns moon.create.ui-viewport
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera])
  (:import (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step [ctx {:keys [width height]}]
  (assoc ctx :ctx/ui-viewport (FitViewport. width height (orthographic-camera/create))))
