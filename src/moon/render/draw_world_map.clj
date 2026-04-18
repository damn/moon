(ns moon.render.draw-world-map
  (:require [clojure.graphics.viewport :as viewport]
            [clojure.gdx.maps.tiled.renderer :as tiled-map-renderer]))

(defn do!
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   tile-color-setter]
  (tiled-map-renderer/draw! batch
                            world-unit-scale
                            (viewport/camera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)
