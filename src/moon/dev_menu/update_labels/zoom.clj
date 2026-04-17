(ns moon.dev-menu.update-labels.zoom
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (camera/zoom (viewport/camera world-viewport)))
   :icon "images/zoom.png"})
