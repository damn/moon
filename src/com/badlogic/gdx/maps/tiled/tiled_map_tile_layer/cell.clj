(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn create [tile]
  (doto (TiledMapTileLayer$Cell.)
    (.setTile tile)))
