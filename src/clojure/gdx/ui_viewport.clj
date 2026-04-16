(ns clojure.gdx.ui-viewport
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clj.api.com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn create [width height]
  (fit-viewport/create width height (orthographic-camera/create)))
