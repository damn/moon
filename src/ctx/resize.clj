(ns ctx.resize
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (.update ^Viewport (:stage/viewport stage) width height true)
  (.update ^Viewport world-viewport width height false))
