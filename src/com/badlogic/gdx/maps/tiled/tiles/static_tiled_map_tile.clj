(ns com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn new
  ([source]
   (if (instance? StaticTiledMapTile source)
     (StaticTiledMapTile. ^StaticTiledMapTile source)
     (StaticTiledMapTile. ^TextureRegion source))))

(defn getProperties [tile]
  (.getProperties ^StaticTiledMapTile tile))
