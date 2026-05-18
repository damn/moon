(ns clojure.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:require [clojure.tiled-map.tile :as tile]
            [clojure.tiled-map.props :as props])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(def copy
  (memoize
   (fn [^StaticTiledMapTile tile]
     (assert tile)
     (StaticTiledMapTile. tile))))

(defn create
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (StaticTiledMapTile. ^TextureRegion texture-region)]
    (props/add! (tile/properties tile) {property-name property-value})
    tile))
