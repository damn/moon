(ns clojure.gdx.tiled-map-tile-layer.visible?
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [layer]
  (TiledMapTileLayer/.isVisible layer))
