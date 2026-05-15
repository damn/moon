(ns moon.impl.explored-tile-corners
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.grid2d :as g2d]))

(defn create
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (props/get (tiled-map/properties tiled-map) "width")
                         (props/get (tiled-map/properties tiled-map) "height")
                         (constantly false))))
