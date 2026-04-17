(ns moon.dev-menu.update-labels.zoom
  (:require [clojure.camera :as camera]
            [clojure.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (camera/zoom (viewport/camera world-viewport)))
   :icon "images/zoom.png"})
