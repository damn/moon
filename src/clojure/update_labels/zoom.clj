(ns clojure.update-labels.zoom
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (orthographic-camera/zoom (viewport/getCamera world-viewport)))
   :icon "images/zoom.png"})
