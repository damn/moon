(ns com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn new [^TextureRegion texture-region]
  (StaticTiledMapTile. texture-region))

(defn new-tile [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))

(defn get-properties [tile]
  (StaticTiledMapTile/.getProperties tile))
