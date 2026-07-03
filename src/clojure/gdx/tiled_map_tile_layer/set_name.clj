(ns clojure.gdx.tiled-map-tile-layer.set-name
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer ^String name]
  (TiledMapTileLayer/.setName layer name))
