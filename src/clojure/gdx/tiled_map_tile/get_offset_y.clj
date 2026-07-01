(ns clojure.gdx.tiled-map-tile.get-offset-y
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn f [tile]
  (TiledMapTile/.getOffsetY tile))
