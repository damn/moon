(ns render.draw-tiled-map
  (:require [gdl.orthographic-camera.get-position :refer [get-position]]
            [gdl.batch.draw-tiled-map :refer [draw-tiled-map!]]
            [render.draw-tiled-map.color-setter :refer [tile-color-setter*]]
            [moon.raycaster.is-blocked :as blocked?]))

(defn- tile-color-setter
  [{:keys [ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/world-viewport]}]
  (tile-color-setter*
   {:ray-blocked? (partial blocked?/f raycaster)
    :explored-tile-corners explored-tile-corners
    :light-position (get-position (:viewport/camera world-viewport))
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
  (draw-tiled-map! batch
                   world-unit-scale
                   (:viewport/camera world-viewport)
                   tiled-map
                   (tile-color-setter ctx))
  ctx)
