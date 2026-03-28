(ns moon.dev-menu.update-labels.zoom
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (orthographic-camera/zoom (Viewport/.getCamera world-viewport)))
   :icon "images/zoom.png"})
