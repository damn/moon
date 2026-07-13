(ns gdx.maps.tiled.tiled-map-tile-layer.cell
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as cell]))

(defn create []
  (cell/new))

(defn set-tile! [cell tile]
  (cell/setTile cell tile))

(defn get-tile [cell]
  (cell/getTile cell))
