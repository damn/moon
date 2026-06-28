(ns levelgen-test.resize
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn f!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (.update ^Viewport (:stage/viewport stage) width height true)
  (.update ^Viewport world-viewport width height false))
