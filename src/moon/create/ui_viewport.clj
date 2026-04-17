(ns moon.create.ui-viewport
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clj.api.com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step [ctx {:keys [width height]}]
  (assoc ctx :ctx/ui-viewport (fit-viewport/create width height (orthographic-camera/create))))
