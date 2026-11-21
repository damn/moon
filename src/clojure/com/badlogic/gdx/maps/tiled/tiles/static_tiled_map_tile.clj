(ns clojure.com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn create [^TextureRegion texture-region]
  (StaticTiledMapTile. texture-region))

(defn copy [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))
