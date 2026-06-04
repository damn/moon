(ns render.draw-tiled-map
  (:require [gdx.graphics.orthographic-camera :as camera]
            [clojure.gdx.maps.tiled.renderer :as tiled-map-renderer]
            [render.draw-tiled-map.color-setter :refer [tile-color-setter*]]
            [moon.raycaster :as raycaster]))

(defn- tile-color-setter
  [{:keys [ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/world-viewport]}]
  (tile-color-setter*
   {:ray-blocked? (partial raycaster/blocked? raycaster)
    :explored-tile-corners explored-tile-corners
    :light-position (camera/position (:viewport/camera world-viewport))
    :see-all-tiles? false
    :explored-tile-color  (:colors/explored-tile colors)
    :visible-tile-color   (:colors/visible-tile colors)
    :invisible-tile-color (:colors/invisible-tile colors)}))

(defn step
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  (tiled-map-renderer/draw! batch
                            world-unit-scale
                            (:viewport/camera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)
