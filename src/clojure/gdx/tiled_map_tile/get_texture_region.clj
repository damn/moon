(ns clojure.gdx.tiled-map-tile.get-texture-region
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn f [tile]
  (TiledMapTile/.getTextureRegion tile))
