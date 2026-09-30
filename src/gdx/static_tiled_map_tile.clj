(ns gdx.static-tiled-map-tile
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn create [texture-region-or-tile]
  (if (instance? StaticTiledMapTile texture-region-or-tile)
    (StaticTiledMapTile. ^StaticTiledMapTile texture-region-or-tile)
    (StaticTiledMapTile. ^TextureRegion texture-region-or-tile)))

(defn get-properties [tile]
  (.getProperties ^StaticTiledMapTile tile))
