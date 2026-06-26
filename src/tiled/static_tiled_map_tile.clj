(ns tiled.static-tiled-map-tile
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn create [texture-region]
  (StaticTiledMapTile. (texture-region/type-hint texture-region)))
