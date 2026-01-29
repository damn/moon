(ns moon.render.draw-world-map
  (:require [moon.tiled-map-renderer :as tiled-map-renderer])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ^Viewport ctx/world-viewport]
    :as ctx}
   tile-color-setter]
  (tiled-map-renderer/draw! batch
                            world-unit-scale
                            (.getCamera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)
