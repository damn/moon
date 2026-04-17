(ns moon.dev-menu.update-labels.zoom
  (:require [gdl.camera :as camera]
            [gdl.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (camera/zoom (viewport/camera world-viewport)))
   :icon "images/zoom.png"})
