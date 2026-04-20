(ns moon.impl.ui-viewport
  (:require [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn create [_ctx {:keys [width height]}]
  (fit-viewport/create width height (orthographic-camera/create)))
