(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell
  (:require [gdl.tiled-map.layer.cell :as cell])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(.bindRoot #'cell/create
           (fn [tile]
             (doto (TiledMapTileLayer$Cell.)
               (.setTile tile))))

(extend-type TiledMapTileLayer$Cell
  cell/Cell
  (tile [this]
    (.getTile this)))
