(ns clojure.moon.render-draw-tiled-map
  (:require [clojure.color-setter :refer [tile-color-setter*]]
            [clojure.gdx-draw-tiled-map :as draw-tiled-map]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.raycaster-is-blocked :as blocked?]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]))

(defn f
  [{:keys [ctx/batch
           ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/tiled-map
           ctx/world-viewport]
    :as ctx}]
  (draw-tiled-map/f! batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     (tile-color-setter*
                      {:ray-blocked? (partial blocked?/f raycaster)
                       :explored-tile-corners explored-tile-corners
                       :light-position (get-position/f (:viewport/camera world-viewport))
                       :see-all-tiles? false
                       :explored-tile-color (:colors/explored-tile colors)
                       :visible-tile-color (:colors/visible-tile colors)
                       :invisible-tile-color (:colors/invisible-tile colors)}))
  ctx)
