(ns clojure.static-tiled-map-tile
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn new [& args]
  (apply static-tiled-map-tile/new args))

(defn get-properties [& args]
  (apply static-tiled-map-tile/getProperties args))
