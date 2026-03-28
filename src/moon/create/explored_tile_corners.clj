(ns moon.create.explored-tile-corners
  (:require [clj.api.com.badlogic.gdx.maps.map-properties :as map-properties]
            [clj.api.com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.grid2d :as g2d]))

(defn step [{:keys [ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/explored-tile-corners (atom (g2d/create-grid (map-properties/get (tiled-map/properties tiled-map) "width")
                                                               (map-properties/get (tiled-map/properties tiled-map) "height")
                                                               (constantly false)))))
