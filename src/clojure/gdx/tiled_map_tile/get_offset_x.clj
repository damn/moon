(ns clojure.gdx.tiled-map-tile.get-offset-x
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn f [tile]
  (TiledMapTile/.getOffsetX tile))
