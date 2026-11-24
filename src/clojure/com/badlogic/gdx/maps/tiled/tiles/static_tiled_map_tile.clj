(ns clojure.com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  "@brief Represents a non changing [[clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile]] (can be cached)"
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn create
  "Creates a static tile with the given region
  
  - `textureRegion`: the [[clojure.com.badlogic.gdx.graphics.g2d.texture-region]] to use."
  [^TextureRegion texture-region]
  (StaticTiledMapTile. texture-region))

(defn copy
  "Creates a static tile with the given region"
  [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))
