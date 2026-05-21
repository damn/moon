(ns clojure.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:require [clojure.tiled-map.tile :as tile])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(.bindRoot #'tile/create
           (fn [^TextureRegion texture-region]
             (StaticTiledMapTile. texture-region)))

(extend-type StaticTiledMapTile
  tile/Tile
  (copy [this]
    (StaticTiledMapTile. this))

  (properties [this]
    (.getProperties this)))
