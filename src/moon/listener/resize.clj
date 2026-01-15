(ns moon.listener.resize
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/graphics
           ctx/ui-viewport]}
   width height]
  (Viewport/.update ui-viewport width height true)
  (Viewport/.update (:graphics/world-viewport graphics) width height false)
  nil)
