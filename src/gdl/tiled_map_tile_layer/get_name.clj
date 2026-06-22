(ns gdl.tiled-map-tile-layer.get-name
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn get-name [^TiledMapTileLayer layer]
  (.getName layer))
