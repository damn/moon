(ns clojure.gdx.tiled-map-tile-layer$cell.set-tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn f [cell tile]
  (TiledMapTileLayer$Cell/.setTile cell tile))
