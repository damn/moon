(ns clojure.tiled-map-tile
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]))

(defn get-offset-x [& args]
  (apply tiled-map-tile/getOffsetX args))

(defn get-offset-y [& args]
  (apply tiled-map-tile/getOffsetY args))

(defn get-properties [& args]
  (apply tiled-map-tile/getProperties args))

(defn get-texture-region [& args]
  (apply tiled-map-tile/getTextureRegion args))

(defn get-tile [& args]
  (apply tiled-map-tile/getTile args))
