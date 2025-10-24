(ns clojure.gdx.maps.tiled.layer.cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn create [tile]
  (doto (TiledMapTileLayer$Cell.)
    (.setTile tile)))

(defn tile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
