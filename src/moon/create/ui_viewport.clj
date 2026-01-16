(ns moon.create.ui-viewport
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step [ctx {:keys [width height]}]
  (assoc ctx :ctx/ui-viewport (FitViewport. width height (OrthographicCamera.))))
