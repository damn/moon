(ns clojure.gdx.maps.tiled.tiled-map-tile-layer.set-visible
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn set-visible! [^TiledMapTileLayer layer bool]
  (.setVisible layer bool))
