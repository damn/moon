(ns clojure.update-labels.zoom
  (:require [clojure.orthographic-camera :as orthographic-camera]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (orthographic-camera/zoom (:viewport/camera world-viewport)))
   :icon "images/zoom.png"})
