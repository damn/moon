(ns clojure.gdx.tiled-map-tile-layer.set-cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer
                                               TiledMapTileLayer$Cell)))

(defn f [^TiledMapTileLayer layer x y ^TiledMapTileLayer$Cell cell]
  (TiledMapTileLayer/.setCell layer (int x) (int y) cell))
