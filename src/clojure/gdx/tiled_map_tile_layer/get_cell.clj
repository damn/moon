(ns clojure.gdx.tiled-map-tile-layer.get-cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer x y]
  (.getCell layer (int x) (int y)))
