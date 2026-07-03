(ns clojure.gdx.tiled-map-tile-layer.get-name
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer]
  (TiledMapTileLayer/.getName layer))
