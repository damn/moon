(ns clojure.gdx.tiled-map-tile-layer.new
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width) (int height) (int tilewidth) (int tileheight)))
