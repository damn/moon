(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile
  "@brief Generalises the concept of tile in a TiledMap"
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn properties
  "Returns tile's properties set"
  [^TiledMapTile tile]
  (.getProperties tile))

(defn texture-region
  "Returns texture region used to render the tile"
  [^TiledMapTile tile]
  (.getTextureRegion tile))

(defn offset-x
  "Returns the amount to offset the x position when rendering the tile"
  [^TiledMapTile tile]
  (.getOffsetX tile))

(defn offset-y
  "Returns the amount to offset the y position when rendering the tile"
  [^TiledMapTile tile]
  (.getOffsetY tile))
