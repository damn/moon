(ns clojure.update-labels.zoom
  (:require [clojure.orthographic-camera :as orthographic-camera]
            [clojure.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (orthographic-camera/zoom (viewport/get-camera world-viewport)))
   :icon "images/zoom.png"})
