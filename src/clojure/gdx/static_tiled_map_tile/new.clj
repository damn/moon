(ns clojure.gdx.static-tiled-map-tile.new
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn f [^TextureRegion texture-region]
  (StaticTiledMapTile. texture-region))
