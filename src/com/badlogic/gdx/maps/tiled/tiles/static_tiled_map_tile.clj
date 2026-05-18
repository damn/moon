(ns com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn copy [^StaticTiledMapTile tile]
  (assert tile)
  (StaticTiledMapTile. tile))

(defn create [texture-region]
  (StaticTiledMapTile. ^TextureRegion texture-region))

(defn properties [^StaticTiledMapTile tile]
  (.getProperties tile))
