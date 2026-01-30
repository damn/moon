(ns moon.dev-menu.update-labels.zoom
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def item
  {:label "Zoom"
   :update-fn (fn [{:keys [ctx/world-viewport]}]
                (.zoom ^OrthographicCamera (Viewport/.getCamera world-viewport)))
   :icon "images/zoom.png"})
