(ns clojure.gdx.tiled-map-tile-layer.get-height
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer]
  (.getHeight layer))
