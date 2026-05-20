(ns com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:require [gdl.tiled-map.tile :as tile])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn create [^TextureRegion texture-region]
  (StaticTiledMapTile. texture-region))

(extend-type StaticTiledMapTile
  tile/Tile
  (copy [this]
    (StaticTiledMapTile. this))

  (properties [this]
    (.getProperties this)))
