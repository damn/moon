(ns moon.dev-menu.update-labels.zoom
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clj.api.com.badlogic.gdx.utils.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (orthographic-camera/zoom (viewport/camera world-viewport)))
   :icon "images/zoom.png"})
