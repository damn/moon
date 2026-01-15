(ns moon.listener.resize
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (Viewport/.update ui-viewport width height true)
  (Viewport/.update world-viewport width height false)
  nil)
