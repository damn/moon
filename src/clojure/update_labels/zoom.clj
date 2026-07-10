(ns clojure.update-labels.zoom
  (:require [gdl.orthographic-camera :as orthographic-camera]
            [gdl.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (orthographic-camera/zoom (viewport/get-camera world-viewport)))
   :icon "images/zoom.png"})
