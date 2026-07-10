(ns gdl.tiled-map-tile-layer-cell
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]))

(defn get-tile [& args]
  (apply tiled-map-tile-layer-cell/getTile args))

(defn new [& args]
  (apply tiled-map-tile-layer-cell/new args))

(defn set-tile! [& args]
  (apply tiled-map-tile-layer-cell/setTile args))
