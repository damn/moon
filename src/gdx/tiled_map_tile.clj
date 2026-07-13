(ns gdx.tiled-map-tile
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]))

(defn get-properties [tile]
  (tiled-map-tile/getProperties tile))

(defn get-texture-region [tile]
  (tiled-map-tile/getTextureRegion tile))

(defn get-offset-x [tile]
  (tiled-map-tile/getOffsetX tile))

(defn get-offset-y [tile]
  (tiled-map-tile/getOffsetY tile))
