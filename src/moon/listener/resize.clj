(ns moon.listener.resize
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/graphics]} width height]
  (Viewport/.update (:graphics/ui-viewport    graphics) width height true)
  (Viewport/.update (:graphics/world-viewport graphics) width height false)
  nil)
