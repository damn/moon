(ns clojure.gdx.tiled-map-tile-layer.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer]
  (.getProperties layer))
