(ns com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn create [texture-region]
  (StaticTiledMapTile. ^TextureRegion texture-region))

(defn properties [^StaticTiledMapTile tile]
  (.getProperties tile))

(defn copy [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))
