(ns tiled.create-static-tiled-map-tile
  (:require [tiled-map-tile.get-properties :as get-properties])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (StaticTiledMapTile. ^TextureRegion texture-region)]
    (MapProperties/.put (get-properties/f tile) property-name property-value)
    tile))
