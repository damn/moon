(ns tiled.create-static-tiled-map-tile
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (StaticTiledMapTile. ^TextureRegion texture-region)]
    (.put (.getProperties tile) property-name property-value)
    tile))
