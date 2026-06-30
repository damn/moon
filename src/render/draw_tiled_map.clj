(ns render.draw-tiled-map
  (:require [orthographic-camera.position :as get-position]
            [clojure.gdx :as gdx]
            [render.draw-tiled-map.color-setter :refer [tile-color-setter*]]
            [moon.raycaster.is-blocked :as blocked?]))

(defn step
  [{:keys [ctx/batch
           ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  (gdx/draw-tiled-map! batch
                       world-unit-scale
                       (:viewport/camera world-viewport)
                       tiled-map
                       (tile-color-setter*
                        {:ray-blocked? (partial blocked?/f raycaster)
                         :explored-tile-corners explored-tile-corners
                         :light-position (get-position/f (:viewport/camera world-viewport))
                         :see-all-tiles? false
                         :explored-tile-color  (:colors/explored-tile colors)
                         :visible-tile-color   (:colors/visible-tile colors)
                         :invisible-tile-color (:colors/invisible-tile colors)}))
  ctx)
